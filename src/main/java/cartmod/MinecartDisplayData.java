package cartmod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Stores last received server side minecart data
 */
public record MinecartDisplayData(Vec3d pos, Box lastReceivedPosBox, Vec3d velocity, boolean onGround, int fillLevel,
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

        int fillLevel = entity instanceof Inventory inventory ? ScreenHandler.calculateComparatorOutput(inventory) : -1;
        double slowdown = getSlowdown((Entity) entity, nbt, fillLevel);
        double estimatedDistance = estimateDistance(velocity.length(), (AbstractMinecartEntity) entity, slowdown);


        Box boxAt = ((Entity) entity).getType().getDimensions().getBoxAt(pos.x, pos.y, pos.z);
        return new MinecartDisplayData(pos, boxAt, velocity, onGround, fillLevel, slowdown, estimatedDistance, (AbstractMinecartEntity) entity);
    }

    public Text getDisplayPosText() {
        if (this.pos() == null) {
            return new TranslatableText("cartmod.pos").append(": ").append(new TranslatableText("cartmod.unknown"));
        }
        return new TranslatableText("cartmod.pos").append(": ").append(formatVec3d(this.pos()));
    }

    public static String formatVec3d(Vec3d vec) {
        return "(" + String.format("%.4f", vec.x) + ", " + String.format("%.2f", vec.y) + ", " + String.format("%.2f", vec.z) + ")";
    }

    public Text getDisplayVelocityText() {
        if (this.velocity() == null) {
            return new TranslatableText("cartmod.velocity").append(": ").append(new TranslatableText("cartmod.unknown"));
        }
        return new TranslatableText("cartmod.velocity").append(": ").append(formatVec3d(this.velocity()));
    }

    public Text getDisplaySpeedText() {
        if (this.velocity() == null) {
            return new TranslatableText("cartmod.speed").append(": ").append(new TranslatableText("cartmod.unknown"));
        }
        return new TranslatableText("cartmod.speed").append(": ").append(String.format("%.4f", this.velocity().multiply(20d).length())).append(new TranslatableText("cartmod.blocks_per_second"));
    }

    public Text getDisplayEstimatedDistanceText() {
        if (this.velocity() == null) {
            return new TranslatableText("cartmod.estimated_distance").append(": ").append(new TranslatableText("cartmod.unknown"));
        }
        return new TranslatableText("cartmod.estimated_distance").append(": ").append(String.format("%.4f", this.estimatedDistance())).append(new TranslatableText("cartmod.blocks_distance"));
    }

    private static double estimateDistance(double velocity, AbstractMinecartEntity entity, double slowdownFactor) {
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
            if (nbt.getString("LootTable") == null) {
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

    public Optional<Text> getFillLevel() {
        if (this.fillLevel == -1) {
            return Optional.empty();
        }
        return Optional.of(new TranslatableText("cartmod.fill_level").append(": ").append(String.valueOf(this.fillLevel)));
    }

    public ArrayList<Text> getInfoTexts() {
        ArrayList<Text> infoTexts = new ArrayList<>();
        if (CartMod.DISPLAY_CART_DATA_POS.isEnabled() && this.pos() != null) {
            infoTexts.add(this.getDisplayPosText());
        }
        if (CartMod.DISPLAY_CART_DATA_VELOCITY.isEnabled() && this.velocity() != null) {
            infoTexts.add(this.getDisplayVelocityText());
        }
        if (CartMod.DISPLAY_CART_DATA_SPEED.isEnabled() && this.velocity() != null) {
            infoTexts.add(this.getDisplaySpeedText());
        }
        Optional<Text> fillLevelText;
        if (CartMod.DISPLAY_CART_DATA_FILL_LEVEL.isEnabled() && (fillLevelText = this.getFillLevel()).isPresent()) {
            infoTexts.add(fillLevelText.get());
        }
        if (CartMod.DISPLAY_CART_DATA_SLOWDOWN_RATE.isEnabled() && this.velocity() != null) {
            infoTexts.add(new TranslatableText("cartmod.slowdown_rate").append(": ").append(String.format("%.4f", this.slowdownFactor())));
        }
        if (CartMod.DISPLAY_CART_DATA_ESTIMATED_DISTANCE.isEnabled() && this.velocity() != null) {
            infoTexts.add(this.getDisplayEstimatedDistanceText());
        }
        if (CartMod.DISPLAY_CART_DATA_IN_WATER.isEnabled()) {
            infoTexts.add(new TranslatableText("cartmod.in_water").append(": ").append(String.valueOf(this.inWater())));
        }
        if (CartMod.DISPLAY_CART_DATA_ON_GROUND.isEnabled()) {
            infoTexts.add(new TranslatableText("cartmod.onGround").append(": ").append(String.valueOf(this.onGround())));
        }


        return infoTexts;
    }

    private boolean inWater() {
        return this.entity().isTouchingWater();
    }
}
