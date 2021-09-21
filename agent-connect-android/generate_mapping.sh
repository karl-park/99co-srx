# Copy `mapping.txt` to the same directory as `app-release.aab` for convenience
#
# How to use:
# Mac: Run `zsh generate_mapping.sh` on terminal
# Window: TODO
#
# TODO Figure out how to do this automatically on `app/build.gradle` instead
# Reference (Not working): https://medium.com/@xabaras/automatically-copying-proguard-mapping-file-to-apk-build-directory-d7eed285632
cp app/build/outputs/mapping/release/mapping.txt app/release