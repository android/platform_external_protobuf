<<<<<<< HEAD   (ca15ad Merge "Merge 3.11.4 into emu-master-dev" into emu-master-dev)
=======
PHP_ARG_ENABLE(protobuf, whether to enable Protobuf extension, [  --enable-protobuf   Enable Protobuf extension])

if test "$PHP_PROTOBUF" != "no"; then

  PHP_NEW_EXTENSION(
    protobuf,
    arena.c array.c convert.c def.c map.c message.c names.c php-upb.c protobuf.c,
    $ext_shared, , -std=gnu99)

fi
>>>>>>> BRANCH (f82e26 Remove references to stale benchmark data sources.)
