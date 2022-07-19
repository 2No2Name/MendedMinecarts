package mendedminecarts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Stores last received server side minecart data
 */
public record MinecartDisplayData(Vec3d pos, Box boundingBox, Vec3d velocity, boolean onGround, int fillLevel,
                                  double slowdownFactor, double estimatedDistance, AbstractMinecartEntity entity) {

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

        double slowdown = getSlowdown((Entity) entity, nbt, fillLevel);
        double estimatedDistance = estimateDistance(velocity.length(), (AbstractMinecartEntity) entity, slowdown);


        Box boxAt = ((Entity) entity).getType().getDimensions().getBoxAt(pos.x, pos.y, pos.z);
        return new MinecartDisplayData(pos, boxAt, velocity, onGround, fillLevel, slowdown, estimatedDistance, (AbstractMinecartEntity) entity);
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
            return new TranslatableText("mendedminecarts.pos").append(": ").append(new TranslatableText("mendedminecarts.unknown"));
        }
        return new TranslatableText("mendedminecarts.pos").append(": ").append(formatVec3d(this.pos()));
    }

    public static String formatVec3d(Vec3d vec) {
        return "(" + String.format(getDoubleFormatString(), vec.x) + ", " + String.format(getDoubleFormatString(), vec.y) + ", " + String.format(getDoubleFormatString(), vec.z) + ")";
    }

    public Text getDisplayVelocityText() {
        if (this.velocity() == null) {
            return new TranslatableText("mendedminecarts.velocity").append(": ").append(new TranslatableText("mendedminecarts.unknown"));
        }
        return new TranslatableText("mendedminecarts.velocity").append(": ").append(formatVec3d(this.velocity()));
    }

    public Text getDisplaySpeedText() {
        if (this.velocity() == null) {
            return new TranslatableText("mendedminecarts.speed").append(": ").append(new TranslatableText("mendedminecarts.unknown"));
        }
        double speed = this.velocity().multiply(20d).length();
        if (this.entity().hasPassengers()) {
            speed *= 0.75;
        }
        speed = Math.min(speed, CartHelper.getMaxSpeed(this.entity()));
        return new TranslatableText("mendedminecarts.speed").append(": ").append(String.format(getDoubleFormatString(), speed)).append(new TranslatableText("mendedminecarts.blocks_per_second"));
    }

    public Text getDisplayEstimatedDistanceText() {
        if (this.velocity() == null) {
            return new TranslatableText("mendedminecarts.estimated_distance").append(": ").append(new TranslatableText("mendedminecarts.unknown"));
        }
        return new TranslatableText("mendedminecarts.estimated_distance").append(": ").append(String.format(getDoubleFormatString(), this.estimatedDistance())).append(new TranslatableText("mendedminecarts.blocks_distance"));
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
        return Optional.of(new TranslatableText("mendedminecarts.fill_level").append(": ").append(String.valueOf(this.fillLevel)));
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
            infoTexts.add(new TranslatableText("mendedminecarts.slowdown_rate").append(": ").append(String.format(getDoubleFormatString(), this.slowdownFactor())));
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_ESTIMATED_DISTANCE.isEnabled() && this.velocity() != null) {
            infoTexts.add(this.getDisplayEstimatedDistanceText());
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_IN_WATER.isEnabled() && MendedMinecartsMod.ACCURATE_CLIENT_MINECARTS.isEnabled()) {
            infoTexts.add(new TranslatableText("mendedminecarts.in_water").append(": ").append(String.valueOf(this.inWater())));
        }
        if (MendedMinecartsMod.DISPLAY_CART_DATA_ON_GROUND.isEnabled()) {
            infoTexts.add(new TranslatableText("mendedminecarts.onGround").append(": ").append(String.valueOf(this.onGround())));
        }


        return infoTexts;
    }

    private boolean inWater() {
        return this.entity().isTouchingWater();
    }
}
