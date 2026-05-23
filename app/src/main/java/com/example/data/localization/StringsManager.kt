package com.example.data.localization

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(val code: String, val displayName: String, val layoutDirection: LayoutDirection) {
    FR("fr", "Français", LayoutDirection.Ltr),
    AR("ar", "العربية", LayoutDirection.Rtl),
    EN("en", "English", LayoutDirection.Ltr)
}

object StringsTranslation {
    val languages = mapOf(
        AppLanguage.FR to mapOf(
            "app_name" to "Rifai Network",
            "developed_by" to "Développé par Technicien Abdelmotaleb Rifai",
            "subtitle" to "Outil Diagnostic & Gestion Réseau",
            "loading" to "Chargement...",
            "welcome" to "Bienvenue dans l'espace de diagnostic réseau de Abdelmotaleb Rifai.",
            "start" to "Commencer",
            
            // Navigation Tabs / Home Grid
            "home" to "Accueil",
            "router_mgmt" to "Accès Routeur",
            "speed_test" to "Speed Test",
            "net_scan" to "Scanner",
            "net_tools" to "Outils Diagnostics",
            "settings" to "Paramètres",
            "ai_diag" to "AI Diagnostic",

            // Home Dashboard
            "connection_status" to "Statut Connexion",
            "wifi_name" to "Nom WiFi (SSID)",
            "gateway_ip" to "Adresse IP Routeur (Passerelle)",
            "local_ip" to "Adresse IP Locale",
            "connection_type" to "Type Connexion",
            "status_active" to "Internet opérationnel",
            "status_no_internet" to "Aucun accès Internet",
            "status_router_only" to "Routeur connecté sans Internet",
            "scan_devices" to "Appareils Connectés",
            "unidentified" to "Non identifié",
            "active_devices" to "Périphériques actifs",
            "signals" to "Signal WiFi",
            
            // Router Page
            "router_details" to "Informations Routeur",
            "router_browser" to "Navigateur Routeur",
            "router_ip_detected" to "IP détectée : ",
            "access_router_btn" to "Accéder à la Passerelle",
            "router_brand_predict" to "Marque estimée :",
            "choose_brand" to "Sélectionner la marque de votre routeur",
            "router_ips" to "IP habituelles :",
            "port_status" to "Statut des ports",
            "webview_tip" to "Astuce : Les identifiants par défaut sont souvent admin / admin ou inscrits au dos du routeur.",

            // Speed Test Page
            "download" to "SÉLECT.",
            "download_speed" to "Débit descendant",
            "upload_speed" to "Débit montant",
            "ping" to "Ping / Latence",
            "mbps" to "Mbps",
            "ms" to "ms",
            "start_test" to "Lancer le Test",
            "testing" to "Test en cours...",
            "test_history" to "Historique des tests",
            "no_history" to "Aucun test sauvegardé",
            "clear_history" to "Effacer",

            // Scanner Page
            "scan_network" to "Scanner le réseau",
            "scanning" to "Scan en cours...",
            "scan_done" to "Analyse terminée ! ",
            "scanned_devices" to "périphériques trouvés",
            "target_subnet" to "Sous-réseau :",

            // Tools Page
            "ping_tool" to "Outil Ping",
            "ping_placeholder" to "Saisir IP ou hôte (ex: google.com)",
            "ping_start" to "Ping",
            "ping_results" to "Résultats du Ping",
            "dns_checker" to "Vérificateur DNS",
            "dns_check_btn" to "Analyser DNS",
            "port_scanner" to "Scanner de ports",
            "port_scan_btn" to "Scanner les ports",
            "wifi_analyzer" to "Analyseur de Signal",
            "wifi_strength" to "Puissance du Signal : ",

            // AI Diagnostics
            "ai_advisor" to "Assistant AI - Diagnostic Réseau",
            "ai_advisor_desc" to "Interrogez l'intelligence artificielle pour diagnostiquer une coupure réseau ou optimiser vos réglages.",
            "ask_ai_placeholder" to "Pourquoi ma connexion est-elle lente ?",
            "send" to "Envoyer",
            "ai_thinking" to "Analyse de l'IA en cours...",

            // Settings Page
            "lang_select" to "Langue de l'application",
            "theme_select" to "Thème visuel",
            "notifications" to "Notifications Réseau et Alertes",
            "notif_on_off" to "Notification lors de coupures ou nouveaux appareils",
            "dev_info" to "Informations Développeur",
            "author" to "Technicien Abdelmotaleb Rifai",
            "author_title" to "Expert Certifié Systèmes & Réseaux",
            "app_version" to "Version de l'application",
            "custom_dns_config" to "Configuration DNS Personnalisée",
            "theme_dark" to "Sombre (Recommandé)",
            "theme_light" to "Clair",
            "theme_system" to "Système"
        ),
        AppLanguage.AR to mapOf(
            "app_name" to "شبكة الرفاعي",
            "developed_by" to "تطوير الفني عبد المطلب رفاعي",
            "subtitle" to "أداة تشخيص وإدارة الشبكة",
            "loading" to "جاري التحميل...",
            "welcome" to "مرحباً بك في تطبيق تشخيص الشبكة المطور بواسطة الفني عبد المطلب رفاعي.",
            "start" to "ابدأ الآن",
            
            // Navigation Tabs / Home Grid
            "home" to "الرئيسية",
            "router_mgmt" to "إعدادات الراوتر",
            "speed_test" to "قياس السرعة",
            "net_scan" to "فحص الأجهزة",
            "net_tools" to "أدوات التشخيص",
            "settings" to "الإعدادات",
            "ai_diag" to "تشخيص الذكاء الاصطناعي",

            // Home Dashboard
            "connection_status" to "حالة الاتصال",
            "wifi_name" to "اسم الشبكة (SSID)",
            "gateway_ip" to "عنوان الراوتر (البوابة)",
            "local_ip" to "العنوان المحلي للجهاز IP",
            "connection_type" to "نوع الاتصال",
            "status_active" to "الإنترنت يعمل بشكل طبيعي",
            "status_no_internet" to "لا يوجد اتصال بالإنترنت",
            "status_router_only" to "متصل بالراوتر ولكن لا يوجد إنترنت",
            "scan_devices" to "الأجهزة المتصلة",
            "unidentified" to "غير محدد",
            "active_devices" to "الأجهزة النشطة",
            "signals" to "إشارة الواي فاي",

            // Router Page
            "router_details" to "معلومات الراوتر",
            "router_browser" to "متصفح الراوتر",
            "router_ip_detected" to "IP المكتشف: ",
            "access_router_btn" to "الدخول إلى صفحة الراوتر",
            "router_brand_predict" to "الجهة المصنعة المقدرة:",
            "choose_brand" to "اختر نوع أو ماركة الراوتر الخاص بك",
            "router_ips" to "عناوين IP الشائعة:",
            "port_status" to "حالة المنافذ",
            "webview_tip" to "نصيحة: بيانات الدخول الافتراضية للراوتر تكون غالباً admin وكلمة المرور admin أو تجدها خلف الراوتر.",

            // Speed Test Page
            "download" to "التحميل",
            "download_speed" to "سرعة التنزيل",
            "upload_speed" to "سرعة الرفع",
            "ping" to "زمن الاستجابة (البينغ)",
            "mbps" to "ميجابت/ثانية",
            "ms" to "ملي ثانية",
            "start_test" to "بدء اختبار السرعة",
            "testing" to "جاري الاختبار...",
            "test_history" to "سجل الاختبارات السابقة",
            "no_history" to "لا يوجد سجل اختبارات بعد",
            "clear_history" to "حذف السجل",

            // Scanner Page
            "scan_network" to "بدء فحص الشبكة local",
            "scanning" to "جاري فحص الشبكة...",
            "scan_done" to "اكتمل الفحص ! ",
            "scanned_devices" to "أجهزة متصلة تم العثور عليها",
            "target_subnet" to "النطاق المستهدف:",

            // Tools Page
            "ping_tool" to "أداة البينغ Ping",
            "ping_placeholder" to "أدخل عنوان IP أو الموقع (مثال: google.com)",
            "ping_start" to "فحص الاتصال",
            "ping_results" to "نتائج فحص البينغ",
            "dns_checker" to "فاحص الـ DNS",
            "dns_check_btn" to "تشخيص الـ DNS",
            "port_scanner" to "فاحص منافذ الراوتر",
            "port_scan_btn" to "فحص منافذ الخدمة",
            "wifi_analyzer" to "محلل إشارة الواي فاي",
            "wifi_strength" to "قوة الإشارة: ",

            // AI Diagnostics
            "ai_advisor" to "مستشار الذكاء الاصطناعي للشبكة",
            "ai_advisor_desc" to "اسأل الذكاء الاصطناعي لتشخيص أي مشكلة في الاتصال أو لمعرفة الإعدادات الأفضل للراوتر الخاص بك.",
            "ask_ai_placeholder" to "لماذا شبكة الواي فاي بطيئة ؟",
            "send" to "إرسال",
            "ai_thinking" to "جاري التفكير وتحليل الشبكة...",

            // Settings Page
            "lang_select" to "لغة التطبيق",
            "theme_select" to "سمة العرض",
            "notifications" to "الإشعارات وتنبيهات الشبكة",
            "notif_on_off" to "إشعار عند انقطاع الإنترنت أو دخول جهاز جديد",
            "dev_info" to "معلومات المطور",
            "author" to "الفني عبد المطلب رفاعي",
            "author_title" to "خبير معتمد في الأنظمة والشبكات",
            "app_version" to "اصدار التطبيق",
            "custom_dns_config" to "تكوين الـ DNS المخصص",
            "theme_dark" to "داكن (موصى به)",
            "theme_light" to "فاتح",
            "theme_system" to "تلقائي حسب النظام"
        ),
        AppLanguage.EN to mapOf(
            "app_name" to "Rifai Network",
            "developed_by" to "Developed by Technicien Abdelmotaleb Rifai",
            "subtitle" to "Network Diagnostics & Router Tool",
            "loading" to "Loading...",
            "welcome" to "Welcome to the network diagnostics workspace developed by Technicien Abdelmotaleb Rifai.",
            "start" to "Get Started",
            
            // Navigation Tabs / Home Grid
            "home" to "Home",
            "router_mgmt" to "Router Access",
            "speed_test" to "Speed Test",
            "net_scan" to "Scanner",
            "net_tools" to "Diagnostic Tools",
            "settings" to "Settings",
            "ai_diag" to "AI Diagnostic",

            // Home Dashboard
            "connection_status" to "Connection Status",
            "wifi_name" to "WiFi Name (SSID)",
            "gateway_ip" to "Router IP (Gateway)",
            "local_ip" to "My Local IP Address",
            "connection_type" to "Connection Type",
            "status_active" to "Internet operational",
            "status_no_internet" to "No Internet Access",
            "status_router_only" to "Router connected without Internet",
            "scan_devices" to "Connected Devices",
            "unidentified" to "Unidentified",
            "active_devices" to "Active units",
            "signals" to "WiFi Signal",

            // Router Page
            "router_details" to "Router Details",
            "router_browser" to "Router Console",
            "router_ip_detected" to "Detected IP: ",
            "access_router_btn" to "Go to Router Gateway",
            "router_brand_predict" to "Manufacturer estimate:",
            "choose_brand" to "Select your Router Brand",
            "router_ips" to "Common Subnets:",
            "port_status" to "Router Ports Health",
            "webview_tip" to "Tip: Router default logins are usually admin / admin or shown on the sticker behind the device.",

            // Speed Test Page
            "download" to "DOWNLOAD",
            "download_speed" to "Download speed",
            "upload_speed" to "Upload speed",
            "ping" to "Ping / Network latency",
            "mbps" to "Mbps",
            "ms" to "ms",
            "start_test" to "Begin Test",
            "testing" to "Testing in progress...",
            "test_history" to "History logs",
            "no_history" to "No test history yet",
            "clear_history" to "Clear Logs",

            // Scanner Page
            "scan_network" to "Scan Local Network",
            "scanning" to "Scanning network range...",
            "scan_done" to "Scan Completed! ",
            "scanned_devices" to "active devices found",
            "target_subnet" to "Target subnet:",

            // Tools Page
            "ping_tool" to "Ping Utility",
            "ping_placeholder" to "Enter target IP or domein (eg: google.com)",
            "ping_start" to "Ping Hôte",
            "ping_results" to "Ping logs Output",
            "dns_checker" to "DNS Lookup",
            "dns_check_btn" to "Check Domain",
            "port_scanner" to "Port Scanning",
            "port_scan_btn" to "Port Analyzer",
            "wifi_analyzer" to "WiFi Signal Meter",
            "wifi_strength" to "Signal level: ",

            // AI Diagnostics
            "ai_advisor" to "AI Network Advisor",
            "ai_advisor_desc" to "Ask our AI model for technical directions, connection troubleshooting advice, and configurations tips.",
            "ask_ai_placeholder" to "Why is my download speed low?",
            "send" to "Ask AI",
            "ai_thinking" to "Analyzing your network profile...",

            // Settings Page
            "lang_select" to "App Localization",
            "theme_select" to "Display Profile",
            "notifications" to "Alert notifications",
            "notif_on_off" to "Alerts when Internet drops or new device joins",
            "dev_info" to "Engineer Identity",
            "author" to "Technicien Abdelmotaleb Rifai",
            "author_title" to "Certified Systems & Network Engineer",
            "app_version" to "Application build level",
            "custom_dns_config" to "Alternative DNS setup",
            "theme_dark" to "Dark (Recommended)",
            "theme_light" to "Light",
            "theme_system" to "System"
        )
    )

    fun getString(key: String, language: AppLanguage): String {
        return languages[language]?.get(key) ?: languages[AppLanguage.FR]?.get(key) ?: key
    }
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.FR }
