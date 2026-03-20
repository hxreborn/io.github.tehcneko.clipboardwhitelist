-adaptresourcefilecontents META-INF/xposed/java_init.list
-keepattributes RuntimeVisibleAnnotations

-keep,allowobfuscation,allowoptimization public class * extends io.github.libxposed.api.XposedModule {
    public <init>();
    public void onPackageLoaded(...);
    public void onSystemServerStarting(...);
}
-keep,allowoptimization,allowobfuscation class * implements io.github.libxposed.api.XposedInterface$Hooker {
    public java.lang.Object intercept(...);
}

-repackageclasses
-allowaccessmodification