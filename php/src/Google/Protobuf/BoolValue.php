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
 * Wrapper message for `bool`.
 * The JSON representation for `BoolValue` is JSON `true` and `false`.
 *
 * Generated from protobuf message <code>google.protobuf.BoolValue</code>
 */
class BoolValue extends \Google\Protobuf\Internal\Message
{
    /**
     * The bool value.
     *
     * Generated from protobuf field <code>bool value = 1;</code>
     */
    protected $value = false;

    /**
     * Constructor.
     *
     * @param array $data {
     *     Optional. Data for populating the Message object.
     *
     *     @type bool $value
     *           The bool value.
     * }
     */
    public function __construct($data = NULL) {
        \GPBMetadata\Google\Protobuf\Wrappers::initOnce();
        parent::__construct($data);
    }

    /**
     * The bool value.
     *
     * Generated from protobuf field <code>bool value = 1;</code>
     * @return bool
     */
    public function getValue()
    {
        return $this->value;
    }

    /**
     * The bool value.
     *
     * Generated from protobuf field <code>bool value = 1;</code>
     * @param bool $var
     * @return $this
     */
    public function setValue($var)
    {
        GPBUtil::checkBool($var);
        $this->value = $var;

        return $this;
    }

}

>>>>>>> BRANCH (f82e26 Remove references to stale benchmark data sources.)
