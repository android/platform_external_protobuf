<<<<<<< HEAD   (ca15ad Merge "Merge 3.11.4 into emu-master-dev" into emu-master-dev)
=======
#!/bin/bash

php -i | grep "Configuration"

# gdb --args php -dextension=../ext/google/protobuf/modules/protobuf.so `which
# phpunit` --bootstrap autoload.php tmp_test.php
#
# gdb --args php -dextension=../ext/google/protobuf/modules/protobuf.so `which phpunit` --bootstrap autoload.php generated_class_test.php
gdb --args php -dextension=../ext/google/protobuf/modules/protobuf.so `which phpunit` --bootstrap autoload.php encode_decode_test.php
#
# gdb --args php -dextension=../ext/google/protobuf/modules/protobuf.so memory_leak_test.php
#
# USE_ZEND_ALLOC=0 valgrind --leak-check=yes php -dextension=../ext/google/protobuf/modules/protobuf.so memory_leak_test.php
>>>>>>> BRANCH (62c402 Add 'java/lite/target' to .gitignore (#8439))
