#! /bin/sh

export ANDROID_JAR=/usr/lib/android-sdk/platforms/android-23/android.jar

export APP_NAME=PhotoSpot

export UNSIGNED_APK=$APP_NAME-unsigned.apk

export UNALIGNED_APK=$APP_NAME-unaligned.apk

export FINAL_APK=$APP_NAME.apk

set -e

rm -rf gen
rm -rf obj

mkdir gen
mkdir obj

cd obj

echo "aapt (package)..."
aapt p -I $ANDROID_JAR -M ../AndroidManifest.xml -A ../../assets -S ../res -m -J ../gen -f -F $UNSIGNED_APK

echo "javac..."
javac -bootclasspath $ANDROID_JAR -source 7 -target 7 -sourcepath ../gen:../src -d . $(find ../gen ../src -type f -name '*.java')

echo "dx..."
dx --dex --output=classes.dex .

echo "aapt (add)..."
aapt a $UNSIGNED_APK classes.dex

echo "jarsigner..."
jarsigner -sigalg MD5withRSA -digestalg SHA1 -keystore ../debug.keystore -storepass android -keypass android -signedjar $UNALIGNED_APK $UNSIGNED_APK androiddebugkey

echo "zipalign..."
zipalign -f 4 $UNALIGNED_APK $FINAL_APK

echo "install..."
adb install -r $FINAL_APK

cd ..

echo "start..."
adb shell am start -a android.intent.action.MAIN -n edu.stts/.PhotoSpot
