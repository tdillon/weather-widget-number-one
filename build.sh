set -ev

echo "TAG: ("$TRAVIS_TAG")"

./gradlew assembleRelease

if [ "${TRAVIS_BRANCH}" = "master" ]; then
  jarsigner -verbose -sigalg SHA1withRSA -storepass $storepass -keypass $keypass -digestalg SHA1 -keystore tgd.android.jks app/build/outputs/apk/app-release-unsigned.apk weather-widget-number-one
  /usr/local/android-sdk/build-tools/23.0.3/zipalign -v 4 app/build/outputs/apk/app-release-unsigned.apk app/build/outputs/apk/app-release.apk
  ls -la app/build/outputs/apk
  supply -v
  supply run -j "Google Play Android Developer-5d5bd2dcc1bd.json" -p tgd.mindless.drone.weatherwidgetnumberone.redux -b app/build/outputs/apk/app-release.apk
else
  echo "Only deploy to Play Store on master branch."
fi
