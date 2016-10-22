set -ev

echo $TRAVIS_TAG

./gradlew assembleRelease

if [ "${TRAVIS_TAG}" != "" ]; then
  jarsigner -verbose -sigalg SHA1withRSA -storepass $storepass -keypass $keypass -digestalg SHA1 -keystore tgd.android.jks app/build/outputs/apk/app-release-unsigned.apk weather-widget-number-one
  /usr/local/android-sdk/build-tools/23.0.3/zipalign -v 4 app/build/outputs/apk/app-release-unsigned.apk app/build/outputs/apk/app-release.apk
  ls -la app/build/outputs/apk
  supply -v
  supply run -j gpdpapi.service_account.json -p tgd.mindless.drone.weatherwidgetnumberone.redux -b app/build/outputs/apk/app-release.apk
fi
