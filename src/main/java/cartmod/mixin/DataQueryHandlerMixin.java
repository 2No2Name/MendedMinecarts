package cartmod.mixin;

import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap;
import net.minecraft.client.network.DataQueryHandler;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(DataQueryHandler.class)
public class DataQueryHandlerMixin {
    @Shadow
    private int expectedTransactionId;
    private final Int2ReferenceLinkedOpenHashMap<Consumer<NbtCompound>> callbacks = new Int2ReferenceLinkedOpenHashMap<>();
    private final int MAX_SIZE = 1000;

    /**
     * @author 2No2Name
     * @reason allow queueing multiple queries
     */
    @Overwrite
    public boolean handleQueryResponse(int transactionId, @Nullable NbtCompound nbt) {
        Consumer<NbtCompound> consumer = this.callbacks.get(transactionId);
        if (consumer != null) {
            consumer.accept(nbt);
            return true;
        }
        return false;
    }

    private int nextQuery(Consumer<NbtCompound> callback) {
        ++this.expectedTransactionId;
        this.callbacks.put(this.expectedTransactionId, callback);

        if (this.callbacks.size() > MAX_SIZE) {
            this.callbacks.removeFirst();
        }
        return this.expectedTransactionId;
    }
}
