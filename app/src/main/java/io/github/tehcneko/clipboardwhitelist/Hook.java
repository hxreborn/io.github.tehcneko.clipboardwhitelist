package io.github.tehcneko.clipboardwhitelist;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.util.Set;

import android.util.Log;

import io.github.libxposed.api.XposedModule;

@SuppressLint({"PrivateApi", "BlockedPrivateApi"})
public class Hook extends XposedModule {
    public static Set<String> whitelist;

    public Hook() {
        super();
    }

    @Override
    public void onSystemServerStarting(@NonNull SystemServerStartingParam param) {
        var classLoader = param.getClassLoader();

        var preference = getRemotePreferences("clipboad_whitelist");
        whitelist = preference.getStringSet("whitelist", Set.of());
        preference.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if ("whitelist".equals(key)) {
                whitelist = preference.getStringSet("whitelist", Set.of());
            }
        });
        try {
            hookIsDefaultIme(classLoader);
        } catch (Throwable t) {
            log(Log.ERROR, "ClipboardWhitelist", "hook isDefaultIme failed", t);
        }
    }

    private void hookIsDefaultIme(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        var clipboardServiceClazz = classLoader.loadClass("com.android.server.clipboard.ClipboardService");
        var isDefaultImeMethod = clipboardServiceClazz.getDeclaredMethod("isDefaultIme", int.class, String.class);
        hook(isDefaultImeMethod).intercept(new IsDefaultIMEHooker());
    }

    private static class IsDefaultIMEHooker implements Hooker {
        @Override
        public Object intercept(@NonNull Chain chain) throws Throwable {
            var packageName = (String) chain.getArg(1);
            if (whitelist.contains(packageName)) {
                return true;
            }
            return chain.proceed();
        }
    }
}
