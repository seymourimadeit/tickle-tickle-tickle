package tallestred.tickle_tickle_tickle.common.data_attachments;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import tallestred.tickle_tickle_tickle.TickleTickleTickleMod;

import java.util.function.Supplier;

public class TTTDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TickleTickleTickleMod.MODID);
    public static final Supplier<AttachmentType<Boolean>> TICKLED = ATTACHMENT_TYPES.register(
            "tickled", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());
}
