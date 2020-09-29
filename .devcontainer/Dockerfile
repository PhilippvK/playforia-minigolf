FROM mcr.microsoft.com/vscode/devcontainers/java:dev-8

# [Option] Install Maven
ARG INSTALL_MAVEN="false"
ARG MAVEN_VERSION=""
# [Option] Install Gradle
ARG INSTALL_GRADLE="false"
ARG GRADLE_VERSION=""
RUN if [ "${INSTALL_MAVEN}" = "true" ]; then su vscode -c "source /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven \"${MAVEN_VERSION}\""; fi \
    && if [ "${INSTALL_GRADLE}" = "true" ]; then su vscode -c "source /usr/local/sdkman/bin/sdkman-init.sh && sdk install gradle \"${GRADLE_VERSION}\""; fi

# [Option] Install Node.js
ARG INSTALL_NODE="true"
ARG NODE_VERSION="lts/*"
RUN if [ "${INSTALL_NODE}" = "true" ]; then su vscode -c "source /usr/local/share/nvm/nvm.sh && nvm install ${NODE_VERSION} 2>&1"; fi

# Section to install additional OS packages.
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends genisoimage

# [Optional] Uncomment this line to install global node packages.
# RUN su vscode -c "source /usr/local/share/nvm/nvm.sh && npm install -g <your-package-here>" 2>&1

### XPRA INSTALLATION ###

# Install APT packages
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get install -y libx11-dev libxtst-dev libxcomposite-dev \
    libxdamage-dev libxkbfile-dev xauth x11-xkb-utils xserver-xorg-video-dummy \
    python-all-dev python-gtk2-dev cython \
    libx264-dev libvpx-dev node-uglify yui-compressor \
    python-gi-dev python3-gi gir1.2-gtk-3.0 \
    python3-cairo-dev libgtk2.0-dev libperl-dev build-essential libgtk-3-dev \
    python-lz4 python-cryptography python3-pip

# Install pip packages
RUN pip3 install pyopengl pyopengl-accelerate rencode \
    netifaces websocket-client websockify pillow \
    PyGObject pycairo cython

# Install XPRA
RUN mkdir -p ~/tmp \
    && cd ~/tmp \
    && wget https://xpra.org/src/xpra-4.0.3.tar.xz \
    && tar -Jxf xpra-4.0.3.tar.xz \
    && rm xpra-4.0.3.tar.xz \
    && cd xpra-4.0.3 \
    && python3 ./setup.py build \
    && python3 ./setup.py install

# Test XPRA
#sudo /usr/local/bin/xpra start --bind-tcp 0.0.0.0:9876 --html=on --start=x-terminal-emulator --mdns=no
