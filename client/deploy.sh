cp -r ../assets/html/ ../public/
cp target/client-*.jar ../public/AGolf/
cp -r target/classes/agolf ../public/AGolf/agolf
mkdir ../public/Shared/
cp -r src/main/resources/sound/shared/ ../public/Shared/sound
cd ../public/AGolf
../../cheerpj_2.1/cheerpjfy.py client-*.jar
cd ..
