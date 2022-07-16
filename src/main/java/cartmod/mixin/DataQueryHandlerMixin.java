package cartmod.mixin;

import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayDeque;
import java.util.function.Consumer;

@Mixin(DataQueryHandler.class)
public class DataQueryHandlerMixin {
    @Shadow private int expectedTransactionId;
    private final ArrayDeque<Pair<Integer, Consumer<NbtCompound>>> callbacks = new ArrayDeque<>();

    /**
     * @author 2No2Name
     * @reason allow queueing multiple queries
     */
    @Overwrite
    public boolean handleQueryResponse(int transactionId, @Nullable NbtCompound nbt) {
        Pair<Integer, Consumer<NbtCompound>> callback;
        while (!this.callbacks.isEmpty()) {
            if ((callback = this.callbacks.poll()).getLeft() != transactionId) {
                continue;
            }
            callback.getRight().accept(nbt);
            return true;
        }
        return false;
    }

    private int nextQuery(Consumer<NbtCompound> callback) {
        ++this.expectedTransactionId;
        this.callbacks.add(new Pair<>(this.expectedTransactionId, callback));
        return this.expectedTransactionId;
    }
}
