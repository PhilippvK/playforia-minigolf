mkdir -p ../public/AGolf
cp target/client-*.jar ../public/AGolf/
cp -r target/classes/agolf ../public/AGolf/agolf
cd ../public/AGolf
../../cheerpj_2.1/cheerpjfy.py client-*.jar
cd ..
