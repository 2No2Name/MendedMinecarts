package mendedminecarts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;
import java.util.Optional;

import static java.lang.Double.isFinite;

/**
 * Stores last received server side minecart data
 */
public record MinecartDisplayData(Vec3d pos, Box boundingBox, Vec3d velocity, boolean onGround, int fillLevel,
                                  double slowdownFactor, double estimatedDistance, boolean hopperLocked, float wobble, boolean rideable,
                                  AbstractMinecartEntity entity) {

    public static MinecartDisplayData fromNBT(AbstractMinecartEntityAccess entity, NbtCompound nbt) {
        NbtList nbtPos = nbt.getList("Pos", 6);
        NbtList nbtVelocity = nbt.getList("Motion", 6);
        double d = nbtVelocity.getDouble(0);
        double e = nbtVelocity.getDouble(1);
        double f = nbtVelocity.getDouble(2);
        Vec3d velocity = new Vec3d(d, e, f);
        Vec3d pos = new Vec3d(nbtPos.getDouble(0), nbtPos.getDouble(1), nbtPos.getDouble(2));

        boolean onGround = nbt.getBoolean("OnGround");

        int fillLevel = entity instanceof StorageMinecartEntity inventory ? getComparatorOutput(inventory, nbt) : -1;

        boolean hopperLocked = entity instanceof HopperMinecartEntity && !(nbt.contains("Enabled") && nbt.getBoolean("Enabled"));
        float wobble = ((AbstractMinecartEntity) entity).getDamageWobbleStrength();

        double slowdown = getSlowdown((Entity) entity, nbt, fillLevel);
        double estimatedDistance = estimateDistance(velocity.length(), (AbstractMinecartEntity) entity, slowdown);

        boolean rideable = ((AbstractMinecartEntity) entity).getType() == EntityType.MINECART && !((AbstractMinecartEntity) entity).hasPassengers();
        Box boxAt = ((Entity) entity).getType().getDimensions().getBoxAt(pos.x, pos.y, pos.z);
        return new MinecartDisplayData(pos, boxAt, velocity, onGround, fillLevel, slowdown, estimatedDistance, hopperLocked, wobble, rideable, (AbstractMinecartEntity) entity);
    }

    private static int getComparatorOutput(StorageMinecartEntity inventory, NbtCompound nbtCompound) {
        DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbtCompound, itemStacks);
        int stacks = 0;
        float weight = 0.0f;
        for (ItemStack itemStack : itemStacks) {
            if (!itemStack.isEmpty()) {
                weight += (float) itemStack.getCount() / (float) Math.min(inventory.getMaxCountPerStack(), itemStack.getMaxCount());
                ++stacks;
            }
        }
        return MathHelper.floor(weight / (float) inventory.size() * 14.0f) + (stacks > 0 ? 1 : 0);
    }

    public Text getDisplayPosText() {
        if (this.pos() == null) {
            return Text.translatable("mendedminecarts.pos").append(": ").append(Text.translatable("mendedminecarts.unknown"));
        }
        return Text.translatable("mendedminecarts.pos").append(": ").append(formatVec3d(this.pos()));
    }

    public Text getBinaryDisplayPosText(String axis, double pos) {
        return Text.literal(axis).append(": ").append(doubleToBinaryString(pos));
    }

    public static String formatVec3d(Vec3d vec) {
        return "(" + String.format(getDoubleFormatString(), vec.x) + ", " + String.format(getDoubleFormatString(), vec.y) + ", " + String.format(getDoubleFormatString(), vec.z) + ")";
    }

    /**
     * Takem from Double.toHexString, but adapted for binary output
     */
    public static String doubleToBinaryString(double d) {
        /*
         * Modeled after the "a" conversion specifier in C99, section
         * 7.19.6.1; however, the output of this method is more
         * tightly specified.
         */
        if (!isFinite(d))
            // For infinity and NaN, use the decimal output.
            return Double.toString(d);
        else {
            // Initialized to maximum size of output.
            StringBuilder answer = new StringBuilder(24);

            if (Math.copySign(1.0, d) == -1.0)    // value is negative,
                answer.append("-");                  // so append sign info

            answer.append("0b");

            d = Math.abs(d);

            if (d == 0.0) {
                answer.append("0.0p0");
            } else {
                boolean subnormal = (d < Double.MIN_NORMAL);

                // Isolate significand bits and OR in a high-order bit
                // so that the string representation has a known
                // length.
                long signifBits = (Double.doubleToLongBits(d)
                        & 0x000FFFFFFFFFFFFFL) |
                        0x1000000000000000L;

                // Subnormal values have a 0 implicit bit; normal
                // values have a 1 implicit bit.
                answer.append(subnormal ? "0." : "1.");

                // Isolate the low-order 52 digits of the binary
                // representation.  If all the digits are zero,
                // replace with a single 0; otherwise, remove all
                // trailing zeros.
                String signif = Long.toBinaryString(signifBits).substring(9, 61);
                answer.append(signif);
                //always display all trailing 0s for now.
                //remove trailing 0s:
//                        signif.equals("0000000000000000000000000000000000000000000000000000") ? // 52 zeros
//                        "0" :
//                        signif.replaceFirst("0{1,51}$", ""));

                answer.append('p');
                // If the value is subnormal, use the E_min exponent
                // value for double; otherwise, extract and report d's
                // exponent (the representation of a subnormal uses
                // E_min -1).
                answer.append(subnormal ?
                        Double.MIN_EXPONENT :
                        Math.getExponent(d));
            }
            return answer.toString();
        }
    }

    public Text getDisplayVelocityText() {
        if (this.velocity() == null) {
            return Text.translatable("mendedminecarts.velocity").append(": ").append(Text.translatable("mendedminecarts.unknown"));
        }
        return Text.translatable("mendedminecarts.velocity").append(": ").append(formatVec3d(this.velocity()));
    }

    public Text getDisplaySpeedText() {
        if (this.velocity() == null) {
            return Text.translatable("mendedminecarts.speed").append(": ").append(Text.translatable("mendedminecarts.unknown"));
        }
        double speed = this.velocity().multiply(20d).length();
        if (this.entity().hasPassengers()) {
            speed *= 0.75;
        }
        speed = Math.min(speed, 20d * CartHelper.getMaxSpeed(this.entity()));
        return Text.translatable("mendedminecarts.speed").append(": ").append(String.format(getDoubleFormatString(), speed)).append(Text.translatable("mendedminecarts.blocks_per_second"));
    }

    public Text getDisplayEstimatedDistanceText() {
        if (this.velocity() == null) {
            return Text.translatable("mendedminecarts.estimated_distance").append(": ").append(Text.translatable("mendedminecarts.unknown"));
        }
        return Text.translatable("mendedminecarts.estimated_distance").append(": ").append(String.format(getDoubleFormatString(), this.estimatedDistance())).append(Text.translatable("mendedminecarts.blocks_distance"));
    }

    private static double estimateDistance(double velocity, AbstractMinecartEntity entity, double slowdownFactor) {
        slowdownFactor = Math.abs(slowdownFactor);
        if (slowdownFactor >= 1) {
            return Double.POSITIVE_INFINITY;
        }
        if (entity.hasPassengers()) {
            velocity *= 0.75; //taken from on rail movement before Entity.move call
        }
        int maxSteps = 100000;
        double distance = 0;
        double maxSpeed = CartHelper.getMaxSpeed(entity);
        while (velocity > maxSpeed && maxSteps > 0) {
            maxSteps--;
            distance += maxSpeed;
            velocity *= slowdownFactor;
        }
        while (velocity > 0.001 && maxSteps > 0) {
            maxSteps--;
            distance += velocity;
            velocity *= slowdownFactor;
        }

        return distance;
    }

    private static double getSlowdown(Entity entity, NbtCompound nbt, int fillLevel) {
        double speedMultiplier = entity.hasPassengers() ? 0.997 : 0.96;
        if (entity instanceof StorageMinecartEntity) {
            if (!nbt.contains("LootTable")) {
                int i = 15 - fillLevel;
                speedMultiplier = 0.98f + (float) i * 0.001f;
            } else {
                speedMultiplier = 0.98f;
            }
        }
        if (entity.isTouchingWater()) {
            speedMultiplier *= 0.95f;
        }
        if (entity instanceof FurnaceMinecartEntity) {
            speedMultiplier *= 0.98;
        }

        return speedMultiplier;
    }

    private static String getDoubleFormatString() {
        return "%." + Math.abs(MendedMinecartsMod.DISPLAY_CART_DATA_PRECISION.getState()) + "f";
    }

    public Optional<Text> getFillLevel() {
        if (this.fillLevel == -1) {
            return Optional.empty();
        }
        return Optional.of(Text.translatable("mendedminecarts.fill_level").append(": ").append(String.valueOf(this.fillLevel)));
    }

    public ArrayList<Text> getInfoTexts() {
        ArrayList<Text> infoTexts = new ArrayList<>();
        if (MendedMinecartsMod.DISPLAY_CART_DATA_POS.isEnabled() && this.pos() != null) {
            infoTexts.add(this.getDisplayPosText());
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_VELOCITY.isEnabled() && this.velocity() != null) {
            infoTexts.add(this.getDisplayVelocityText());
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_SPEED.isEnabled() && this.velocity() != null) {
            infoTexts.add(this.getDisplaySpeedText());
        }
        Optional<Text> fillLevelText;
        if (MendedMinecartsMod.DISPLAY_CART_DATA_FILL_LEVEL.isEnabled() && (fillLevelText = this.getFillLevel()).isPresent()) {
            infoTexts.add(fillLevelText.get());
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_SLOWDOWN_RATE.isEnabled() && this.velocity() != null && MendedMinecartsMod.ACCURATE_CLIENT_MINECARTS.isEnabled()) {
            infoTexts.add(Text.translatable("mendedminecarts.slowdown_rate").append(": ").append(String.format(getDoubleFormatString(), this.slowdownFactor())));
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_ESTIMATED_DISTANCE.isEnabled() && this.velocity() != null) {
            infoTexts.add(this.getDisplayEstimatedDistanceText());
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_IN_WATER.isEnabled() && MendedMinecartsMod.ACCURATE_CLIENT_MINECARTS.isEnabled()) {
            infoTexts.add(Text.translatable("mendedminecarts.in_water").append(": ").append(String.valueOf(this.inWater())));
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_ON_GROUND.isEnabled()) {
            infoTexts.add(Text.translatable("mendedminecarts.onGround").append(": ").append(String.valueOf(this.onGround())));
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_WOBBLE.isEnabled()) {
            infoTexts.add(Text.translatable("mendedminecarts.wobble").append(": ").append(String.format(getDoubleFormatString(), this.wobble())));
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_HOPPER_CART_LOCKED.isEnabled() && this.entity() instanceof HopperMinecartEntity) {
            infoTexts.add(Text.translatable("mendedminecarts.hopper_locked").append(": ").append(String.valueOf(this.hopperLocked())));
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_POS_BINARY.isEnabled() && this.pos() != null) {
            infoTexts.add(this.getBinaryDisplayPosText("X", this.pos.x));
            infoTexts.add(this.getBinaryDisplayPosText("Y", this.pos.y));
            infoTexts.add(this.getBinaryDisplayPosText("Z", this.pos.z));
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_RIDEABLE.isEnabled() && this.entity().getType() == EntityType.MINECART) {
            infoTexts.add(Text.translatable("mendedminecarts.is_rideable").append(": ").append(String.valueOf(this.rideable())));
        }


        return infoTexts;
    }

    public Object[] hopperPickupArea1() {
        if (!(this.entity() instanceof HopperMinecartEntity hopper)) {
            return new Box[0];
        }
        VoxelShape inputAreaShape = hopper.getInputAreaShape();
        return inputAreaShape.getBoundingBoxes().stream().map(box -> box.offset(this.pos.x - 0.5, this.pos.y + 0.5 - 0.5, this.pos.z - 0.5)).toArray();
    }

    public Box hopperPickupArea2() {
        if (!(this.entity() instanceof HopperMinecartEntity)) {
            return null;
        }
        return this.boundingBox().expand(0.25, 0.0, 0.25);
    }

    public BlockPos hopperExtractBlock() {
        if (!(this.entity() instanceof HopperMinecartEntity)) {
            return null;
        }
        return BlockPos.ofFloored(this.pos.x, this.pos.y + 0.5 + 1.0, this.pos.z);
    }

    public Box hopperExtractBox() {
        if (!(this.entity() instanceof HopperMinecartEntity)) {
            return null;
        }
        double x = this.pos.x;
        double y = this.pos.y + 0.5 + 1.0;
        double z = this.pos.z;
        return new Box(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5);
    }

    private boolean inWater() {
        return this.entity().isTouchingWater();
    }
}
