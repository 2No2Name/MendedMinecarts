package cartmod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

/**
 * Stores last received server side minecart data
 */
public record MinecartDisplayData(Vec3d pos, Box lastReceivedPosBox, Vec3d velocity, boolean onGround, AbstractMinecartEntity entity) {


//    public static MinecartDisplayData withVelocity(AbstractMinecartEntityAccess entity, Vec3d velocity) {
//        MinecartDisplayData displayInfo = entity.getDisplayInfo();
//        if (displayInfo == null) {
//            return new MinecartDisplayData(null,null, velocity, (AbstractMinecartEntity) entity);
//        } else {
//            return new MinecartDisplayData(displayInfo.pos, displayInfo.lastReceivedPosBox, velocity, (AbstractMinecartEntity) entity);
//        }
//    }
//
//    public static MinecartDisplayData withPos(AbstractMinecartEntityAccess entity, Vec3d pos) {
//        MinecartDisplayData displayInfo = entity.getDisplayInfo();
//        Box boxAt = ((Entity) entity).getType().getDimensions().getBoxAt(pos.x, pos.y, pos.z);
//        if (displayInfo == null) {
//            return new MinecartDisplayData(pos, boxAt, null, (AbstractMinecartEntity) entity);
//        } else {
//            return new MinecartDisplayData(pos, boxAt, displayInfo.velocity, (AbstractMinecartEntity) entity);
//        }
//    }

    public static MinecartDisplayData fromNBT(AbstractMinecartEntityAccess entity, NbtCompound nbt) {
        NbtList nbtPos = nbt.getList("Pos", 6);
        NbtList nbtVelocity = nbt.getList("Motion", 6);
        double d = nbtVelocity.getDouble(0);
        double e = nbtVelocity.getDouble(1);
        double f = nbtVelocity.getDouble(2);
        Vec3d velocity = new Vec3d(d, e, f);
        Vec3d pos = new Vec3d(nbtPos.getDouble(0), nbtPos.getDouble(1), nbtPos.getDouble(2));

        boolean onGround = nbt.getBoolean("OnGround");


        Box boxAt = ((Entity) entity).getType().getDimensions().getBoxAt(pos.x, pos.y, pos.z);
        return new MinecartDisplayData(pos, boxAt, velocity, onGround, (AbstractMinecartEntity) entity);
    }

    public Text getDisplayPosText() {
        if (this.pos() == null) {
            return new TranslatableText("cartmod.pos").append(": ").append(new TranslatableText("cartmod.unknown"));
        }
        return new TranslatableText("cartmod.pos").append(": ").append(this.pos().toString());
    }

    public Text getDisplayVelocityText() {
        if (this.velocity() == null) {
            return new TranslatableText("cartmod.velocity").append(": ").append(new TranslatableText("cartmod.unknown"));
        }
        return new TranslatableText("cartmod.velocity").append(": ").append(this.velocity().toString());
    }

    public ArrayList<Text> getInfoTexts() {
        ArrayList<Text> infoTexts = new ArrayList<>();
        if (this.pos() != null) {
            infoTexts.add(this.getDisplayPosText());
        }
        if (this.velocity() != null) {
            infoTexts.add(this.getDisplayVelocityText());
        }
        infoTexts.add(new TranslatableText("cartmod.onGround").append(": ").append(String.valueOf(this.onGround())));
        return infoTexts;
    }
}
