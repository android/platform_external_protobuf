<<<<<<< HEAD   (ca15ad Merge "Merge 3.11.4 into emu-master-dev" into emu-master-dev)
=======
#!/bin/bash
#
# Build file to set up and run tests

# Change to repo root
cd $(dirname $0)/../../..

# Prepare worker environment to run tests
KOKORO_INSTALL_COCOAPODS=yes
source kokoro/macos/prepare_build_macos_rc

./tests.sh objectivec_cocoapods_integration
>>>>>>> BRANCH (f82e26 Remove references to stale benchmark data sources.)
