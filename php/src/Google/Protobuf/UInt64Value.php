<<<<<<< HEAD   (ca15ad Merge "Merge 3.11.4 into emu-master-dev" into emu-master-dev)
=======
<?php
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: google/protobuf/wrappers.proto

namespace Google\Protobuf;

use Google\Protobuf\Internal\GPBType;
use Google\Protobuf\Internal\RepeatedField;
use Google\Protobuf\Internal\GPBUtil;

/**
 * Wrapper message for `uint64`.
 * The JSON representation for `UInt64Value` is JSON string.
 *
 * Generated from protobuf message <code>google.protobuf.UInt64Value</code>
 */
class UInt64Value extends \Google\Protobuf\Internal\Message
{
    /**
     * The uint64 value.
     *
     * Generated from protobuf field <code>uint64 value = 1;</code>
     */
    protected $value = 0;

    /**
     * Constructor.
     *
     * @param array $data {
     *     Optional. Data for populating the Message object.
     *
     *     @type int|string $value
     *           The uint64 value.
     * }
     */
    public function __construct($data = NULL) {
        \GPBMetadata\Google\Protobuf\Wrappers::initOnce();
        parent::__construct($data);
    }

    /**
     * The uint64 value.
     *
     * Generated from protobuf field <code>uint64 value = 1;</code>
     * @return int|string
     */
    public function getValue()
    {
        return $this->value;
    }

    /**
     * The uint64 value.
     *
     * Generated from protobuf field <code>uint64 value = 1;</code>
     * @param int|string $var
     * @return $this
     */
    public function setValue($var)
    {
        GPBUtil::checkUint64($var);
        $this->value = $var;

        return $this;
    }

}

>>>>>>> BRANCH (f82e26 Remove references to stale benchmark data sources.)
