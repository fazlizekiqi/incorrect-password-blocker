#! /bin/bash

if ls ~/.jenv/* > /dev/null 2>&1
then
    echo "Jenv is already installed."
else
  git clone https://github.com/jenv/jenv.git ~/.jenv
  echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.zshrc
  echo 'eval "$(jenv init -)"' >> ~/.zshrc
  echo "Jenv installed and PATH set to .zshrc"
fi

# Update values when upgrading to a new JDK
jdk_file='amazon-corretto-17-x64-macos-jdk.tar.gz'
java_version='17.0'

curl -LO https://corretto.aws/downloads/latest/$jdk_file

tar -xf $jdk_file -C /Library/Java/JavaVirtualMachines

jenv add /Library/Java/JavaVirtualMachines/amazon-corretto-17.jdk/Contents/Home

source ~/.zprofile

java --version

echo "Java installed successfullly. Deleting $jdk_file"

rm -rf amazon-corretto-17-x64-macos-jdk.tar.gz
