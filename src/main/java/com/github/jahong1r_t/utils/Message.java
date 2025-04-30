package com.github.jahong1r_t.utils;

public interface Message {

    // ===================== User-related messages =====================
    String welcomeUserMsg = """
            👋 Assalomu alaykum! Botdan to‘liq foydalanish uchun quyidagi kanallarga obuna bo‘ling. Obuna bo‘lganingizdan so‘ng, “✅ Tekshirish” tugmasini bosing.
            """;

    String notFollowedChannelsMsg = """
            ❌ Kechirasiz, siz hali ham barcha kerakli kanallarga obuna bo‘lmadingiz. Iltimos, barcha kanallarga a’zo bo‘ling va qayta urinib ko‘ring.
            """;

    String thankForRatingMsg = """
            ⭐ Filmga baho berganingiz uchun katta rahmat!
            """;
    String allReadyRatingMsg = """
            Siz bu filmni avval baholagansiz!
            """;

    String movieNotFoundMsg = """
            😕 Afsuski, siz kiritgan kodga mos film topilmadi. Iltimos, kodni tekshirib, qayta urinib ko‘ring.
            """;

    String promptMovieCodeMsg = """
            🎬 Assalomu alaykum! Iltimos, sizga kerakli film kodini yuboring.
            """;

    String disconnectedMsg = """
            Kanal uzildi ✔️.
            """;

    // ===================== Admin commands =====================
    String welcomeAdminMsg = """
            👨‍💻 Xush kelibsiz, admin! Siz admin paneldasiz. Kerakli amallarni tanlang.
            """;

    String promptMovieUploadMsg = """
            📤 Iltimos, yuklanadigan film videosini yuboring. Caption (izoh) qismida film haqida qisqacha ma’lumot ham yozishni unutmang.
            """;

    String promptMovieCodeEntryMsg = """
            🔢 Iltimos, film uchun noyob (takrorlanmas) kod kiriting. Bu kod orqali foydalanuvchilar filmga murojaat qiladi.
            """;

    String movieAddedMsg = """
            ✅ Film muvaffaqiyatli yuklandi va saqlandi. Endi foydalanuvchilar bu filmga kod orqali murojaat qila olishadi.
            """;

    String movieCodeExistsMsg = """
            ⚠️ Bunday kod allaqachon mavjud. Iltimos, boshqa noyob kod tanlab qayta urinib ko‘ring.
            """;

    String videoRequiredMsg = """
            📹 Iltimos, faqat video formatdagi fayl yuboring. Tekshiruvdan o‘tgan videolarni qabul qilamiz.
            """;

    String movieDataNotFoundMsg = """
            ❌ Kiritilgan kod bo‘yicha film ma’lumotlari topilmadi. Ehtimol, kod noto‘g‘ri yoki film o‘chirilgan.
            """;

    String movieRemovedMsg = """
            🗑 Film muvaffaqiyatli o‘chirildi.
            """;

    String promptChannelLinkMsg = """
            🔗 Iltimos, kanal havolasini quyidagi formatda yuboring: @ChannelName
            """;

    String channelConnectedMsg = """
            ✅ Kanal botga muvaffaqiyatli ulandi. Endi foydalanuvchidan ushbu kanalga a'zo bo'lishi so'raladi.
            """;

    String channelNotConnectedBotNotAdminMsg = """
            ⚠️ Kanalni ulab bo‘lmadi, chunki bot u yerda admin emas. Iltimos, botni kanalingizga admin qilib qo‘shing.
            """;
    String channelNotFound = """
            ⚠️ Sizda ulangan kanallar mavjud emas
            """;

    String promptBroadcastMsg = """
            📢 Iltimos, barcha foydalanuvchilarga yuboriladigan xabar matnini kiriting. Rasm, video yoki fayl bo‘lsa, ilova qiling.
            """;

    String broadcastTextSentMsg = """
            ✅ Matnli xabar barcha foydalanuvchilarga yuborildi.
            """;

    String broadcastPhotoSentMsg = """
            🖼 Rasm barcha foydalanuvchilarga yuborildi.
            """;

    String broadcastVideoSentMsg = """
            🎥 Video barcha foydalanuvchilarga yuborildi.
            """;

    String broadcastDocumentSentMsg = """
            📄 Hujjat (document) barcha foydalanuvchilarga yuborildi.
            """;

    String broadcastAudioSentMsg = """
            🎧 Audio fayl barcha foydalanuvchilarga yuborildi.
            """;

    String broadcastVoiceSentMsg = """
            🎙 Voice xabar barcha foydalanuvchilarga yuborildi.
            """;

    String broadcastStickerSentMsg = """
            🪄 Sticker barcha foydalanuvchilarga yuborildi.
            """;

    String unsupportedMessageTypeMsg = """
            ❌ Kechirasiz, bu turdagi xabarni yuborish hozircha qo‘llab-quvvatlanmaydi. Faqat matn, media yoki fayl yuborish mumkin.
            """;

    String messageRequiredMsg = """
            ⚠️ Iltimos, yuboriladigan xabarni kiriting yoki fayl ilova qiling.
            """;


    // ===================== General messages =====================
    String unknownCommandMsg = """
            ❓ Kechirasiz, siz kiritgan buyruq tanilmadi.
            """;

    String unknownStateMsg = """
            ❌ Xatolik: Noma’lum holatga tushildi. Iltimos, qayta boshlang.
            """;
}
