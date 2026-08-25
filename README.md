# OpenCode Chat (Android)

تطبيق Android بسيط (Kotlin + Jetpack Compose) يتصل بخادم `opencode serve` عبر REST لإنشاء الجلسات وإرسال الرسائل، وعبر SSE (`/event`) لاستقبال ردود الوكيل لحظيًا.

## كيف يُبنى APK بدون حاسوب

هذا المشروع **لا يحتاج Android Studio ولا Gradle Wrapper محليًا** — البناء يتم بالكامل سحابيًا عبر GitHub Actions (انظر `.github/workflows/build-apk.yml`).

1. بعد رفع هذا المشروع لمستودع GitHub، سيبدأ البناء تلقائيًا (على أي push لفرع `main`).
2. من متصفح الهاتف: افتح المستودع → تبويب **Actions** → اختر آخر تشغيل (run) → انتظر حتى تظهر علامة ✅.
3. انزل لقسم **Artifacts** بنفس الصفحة → حمّل `app-debug-apk` (ملف مضغوط يحوي `app-debug.apk`).
4. فك الضغط بمدير الملفات على الهاتف، ثم اضغط على `app-debug.apk` لتثبيته (قد يطلب منك السماح بالتثبيت من مصادر غير معروفة).

## تشغيل خادم OpenCode على نفس الهاتف (Termux)

```bash
pkg install nodejs git -y   # أو الطريقة التي ثبّتّ بها opencode
export PATH=$PREFIX/opencode/bin:$PATH   # حسب مكان التثبيت عندك
OPENCODE_SERVER_PASSWORD=كلمة_سر_قوية opencode serve --hostname 127.0.0.1 --port 4096
```

بما أن التطبيق والخادم يعملان على **نفس الهاتف**، لا حاجة لـ `adb reverse` ولا لأي شبكة خارجية — فقط افتح التطبيق واكتب:

- عنوان الخادم: `http://127.0.0.1:4096/`
- كلمة المرور: نفس قيمة `OPENCODE_SERVER_PASSWORD`

## ملاحظة مهمة حول مخطط الـ API

أسماء الحقول في `Models.kt` (`parts`, `providerID`, `modelID`, إلخ) وشكل أحداث `/event` في `ChatViewModel.kt` مبنية على التوثيق العام لـ OpenCode وقد تختلف قليلاً حسب إصدارك. للتأكد:

1. شغّل الخادم، وافتح `http://127.0.0.1:4096/doc` من متصفح الهاتف.
2. قارن حقول `POST /session/{id}/message` وشكل بيانات `/event` مع ما هو مكتوب في:
   - `app/src/main/java/com/opencode/chat/data/Models.kt`
   - `app/src/main/java/com/opencode/chat/ChatViewModel.kt` (دالة `startListening`)
3. عدّل ما يلزم، ثم اعمل push من جديد — سيُعاد البناء تلقائيًا.

## هيكل المشروع

```
app/src/main/java/com/opencode/chat/
├── MainActivity.kt          نقطة الدخول، يبدّل بين شاشة الاتصال وشاشة الدردشة
├── ChatViewModel.kt          الحالة + منطق الاتصال بالخادم
├── data/
│   ├── Models.kt              نماذج البيانات (طلبات/ردود)
│   └── OpenCodeApi.kt         عميل Retrofit + SSE
└── ui/
    ├── ConnectScreen.kt       شاشة إدخال عنوان الخادم وكلمة المرور
    └── ChatScreen.kt          شاشة الدردشة
```
