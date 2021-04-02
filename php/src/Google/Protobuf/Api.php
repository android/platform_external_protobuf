<<<<<<< HEAD   (ca15ad Merge "Merge 3.11.4 into emu-master-dev" into emu-master-dev)
=======
<?php
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: google/protobuf/api.proto

namespace Google\Protobuf;

use Google\Protobuf\Internal\GPBType;
use Google\Protobuf\Internal\RepeatedField;
use Google\Protobuf\Internal\GPBUtil;

/**
 * Api is a light-weight descriptor for an API Interface.
 * Interfaces are also described as "protocol buffer services" in some contexts,
 * such as by the "service" keyword in a .proto file, but they are different
 * from API Services, which represent a concrete implementation of an interface
 * as opposed to simply a description of methods and bindings. They are also
 * sometimes simply referred to as "APIs" in other contexts, such as the name of
 * this message itself. See https://cloud.google.com/apis/design/glossary for
 * detailed terminology.
 *
 * Generated from protobuf message <code>google.protobuf.Api</code>
 */
class Api extends \Google\Protobuf\Internal\Message
{
    /**
     * The fully qualified name of this interface, including package name
     * followed by the interface's simple name.
     *
     * Generated from protobuf field <code>string name = 1;</code>
     */
    protected $name = '';
    /**
     * The methods of this interface, in unspecified order.
     *
     * Generated from protobuf field <code>repeated .google.protobuf.Method methods = 2;</code>
     */
    private $methods;
    /**
     * Any metadata attached to the interface.
     *
     * Generated from protobuf field <code>repeated .google.protobuf.Option options = 3;</code>
     */
    private $options;
    /**
     * A version string for this interface. If specified, must have the form
     * `major-version.minor-version`, as in `1.10`. If the minor version is
     * omitted, it defaults to zero. If the entire version field is empty, the
     * major version is derived from the package name, as outlined below. If the
     * field is not empty, the version in the package name will be verified to be
     * consistent with what is provided here.
     * The versioning schema uses [semantic
     * versioning](http://semver.org) where the major version number
     * indicates a breaking change and the minor version an additive,
     * non-breaking change. Both version numbers are signals to users
     * what to expect from different versions, and should be carefully
     * chosen based on the product plan.
     * The major version is also reflected in the package name of the
     * interface, which must end in `v<major-version>`, as in
     * `google.feature.v1`. For major versions 0 and 1, the suffix can
     * be omitted. Zero major versions must only be used for
     * experimental, non-GA interfaces.
     *
     * Generated from protobuf field <code>string version = 4;</code>
     */
    protected $version = '';
    /**
     * Source context for the protocol buffer service represented by this
     * message.
     *
     * Generated from protobuf field <code>.google.protobuf.SourceContext source_context = 5;</code>
     */
    protected $source_context = null;
    /**
     * Included interfaces. See [Mixin][].
     *
     * Generated from protobuf field <code>repeated .google.protobuf.Mixin mixins = 6;</code>
     */
    private $mixins;
    /**
     * The source syntax of the service.
     *
     * Generated from protobuf field <code>.google.protobuf.Syntax syntax = 7;</code>
     */
    protected $syntax = 0;

    /**
     * Constructor.
     *
     * @param array $data {
     *     Optional. Data for populating the Message object.
     *
     *     @type string $name
     *           The fully qualified name of this interface, including package name
     *           followed by the interface's simple name.
     *     @type \Google\Protobuf\Method[]|\Google\Protobuf\Internal\RepeatedField $methods
     *           The methods of this interface, in unspecified order.
     *     @type \Google\Protobuf\Option[]|\Google\Protobuf\Internal\RepeatedField $options
     *           Any metadata attached to the interface.
     *     @type string $version
     *           A version string for this interface. If specified, must have the form
     *           `major-version.minor-version`, as in `1.10`. If the minor version is
     *           omitted, it defaults to zero. If the entire version field is empty, the
     *           major version is derived from the package name, as outlined below. If the
     *           field is not empty, the version in the package name will be verified to be
     *           consistent with what is provided here.
     *           The versioning schema uses [semantic
     *           versioning](http://semver.org) where the major version number
     *           indicates a breaking change and the minor version an additive,
     *           non-breaking change. Both version numbers are signals to users
     *           what to expect from different versions, and should be carefully
     *           chosen based on the product plan.
     *           The major version is also reflected in the package name of the
     *           interface, which must end in `v<major-version>`, as in
     *           `google.feature.v1`. For major versions 0 and 1, the suffix can
     *           be omitted. Zero major versions must only be used for
     *           experimental, non-GA interfaces.
     *     @type \Google\Protobuf\SourceContext $source_context
     *           Source context for the protocol buffer service represented by this
     *           message.
     *     @type \Google\Protobuf\Mixin[]|\Google\Protobuf\Internal\RepeatedField $mixins
     *           Included interfaces. See [Mixin][].
     *     @type int $syntax
     *           The source syntax of the service.
     * }
     */
    public function __construct($data = NULL) {
        \GPBMetadata\Google\Protobuf\Api::initOnce();
        parent::__construct($data);
    }

    /**
     * The fully qualified name of this interface, including package name
     * followed by the interface's simple name.
     *
     * Generated from protobuf field <code>string name = 1;</code>
     * @return string
     */
    public function getName()
    {
        return $this->name;
    }

    /**
     * The fully qualified name of this interface, including package name
     * followed by the interface's simple name.
     *
     * Generated from protobuf field <code>string name = 1;</code>
     * @param string $var
     * @return $this
     */
    public function setName($var)
    {
        GPBUtil::checkString($var, True);
        $this->name = $var;

        return $this;
    }

    /**
     * The methods of this interface, in unspecified order.
     *
     * Generated from protobuf field <code>repeated .google.protobuf.Method methods = 2;</code>
     * @return \Google\Protobuf\Internal\RepeatedField
     */
    public function getMethods()
    {
        return $this->methods;
    }

    /**
     * The methods of this interface, in unspecified order.
     *
     * Generated from protobuf field <code>repeated .google.protobuf.Method methods = 2;</code>
     * @param \Google\Protobuf\Method[]|\Google\Protobuf\Internal\RepeatedField $var
     * @return $this
     */
    public function setMethods($var)
    {
        $arr = GPBUtil::checkRepeatedField($var, \Google\Protobuf\Internal\GPBType::MESSAGE, \Google\Protobuf\Method::class);
        $this->methods = $arr;

        return $this;
    }

    /**
     * Any metadata attached to the interface.
     *
     * Generated from protobuf field <code>repeated .google.protobuf.Option options = 3;</code>
     * @return \Google\Protobuf\Internal\RepeatedField
     */
    public function getOptions()
    {
        return $this->options;
    }

    /**
     * Any metadata attached to the interface.
     *
     * Generated from protobuf field <code>repeated .google.protobuf.Option options = 3;</code>
     * @param \Google\Protobuf\Option[]|\Google\Protobuf\Internal\RepeatedField $var
     * @return $this
     */
    public function setOptions($var)
    {
        $arr = GPBUtil::checkRepeatedField($var, \Google\Protobuf\Internal\GPBType::MESSAGE, \Google\Protobuf\Option::class);
        $this->options = $arr;

        return $this;
    }

    /**
     * A version string for this interface. If specified, must have the form
     * `major-version.minor-version`, as in `1.10`. If the minor version is
     * omitted, it defaults to zero. If the entire version field is empty, the
     * major version is derived from the package name, as outlined below. If the
     * field is not empty, the version in the package name will be verified to be
     * consistent with what is provided here.
     * The versioning schema uses [semantic
     * versioning](http://semver.org) where the major version number
     * indicates a breaking change and the minor version an additive,
     * non-breaking change. Both version numbers are signals to users
     * what to expect from different versions, and should be carefully
     * chosen based on the product plan.
     * The major version is also reflected in the package name of the
     * interface, which must end in `v<major-version>`, as in
     * `google.feature.v1`. For major versions 0 and 1, the suffix can
     * be omitted. Zero major versions must only be used for
     * experimental, non-GA interfaces.
     *
     * Generated from protobuf field <code>string version = 4;</code>
     * @return string
     */
    public function getVersion()
    {
        return $this->version;
    }

    /**
     * A version string for this interface. If specified, must have the form
     * `major-version.minor-version`, as in `1.10`. If the minor version is
     * omitted, it defaults to zero. If the entire version field is empty, the
     * major version is derived from the package name, as outlined below. If the
     * field is not empty, the version in the package name will be verified to be
     * consistent with what is provided here.
     * The versioning schema uses [semantic
     * versioning](http://semver.org) where the major version number
     * indicates a breaking change and the minor version an additive,
     * non-breaking change. Both version numbers are signals to users
     * what to expect from different versions, and should be carefully
     * chosen based on the product plan.
     * The major version is also reflected in the package name of the
     * interface, which must end in `v<major-version>`, as in
     * `google.feature.v1`. For major versions 0 and 1, the suffix can
     * be omitted. Zero major versions must only be used for
     * experimental, non-GA interfaces.
     *
     * Generated from protobuf field <code>string version = 4;</code>
     * @param string $var
     * @return $this
     */
    public function setVersion($var)
    {
        GPBUtil::checkString($var, True);
        $this->version = $var;

        return $this;
    }

    /**
     * Source context for the protocol buffer service represented by this
     * message.
     *
     * Generated from protobuf field <code>.google.protobuf.SourceContext source_context = 5;</code>
     * @return \Google\Protobuf\SourceContext|null
     */
    public function getSourceContext()
    {
        return isset($this->source_context) ? $this->source_context : null;
    }

    public function hasSourceContext()
    {
        return isset($this->source_context);
    }

    public function clearSourceContext()
    {
        unset($this->source_context);
    }

    /**
     * Source context for the protocol buffer service represented by this
     * message.
     *
     * Generated from protobuf field <code>.google.protobuf.SourceContext source_context = 5;</code>
     * @param \Google\Protobuf\SourceContext $var
     * @return $this
     */
    public function setSourceContext($var)
    {
        GPBUtil::checkMessage($var, \Google\Protobuf\SourceContext::class);
        $this->source_context = $var;

        return $this;
    }

    /**
     * Included interfaces. See [Mixin][].
     *
     * Generated from protobuf field <code>repeated .google.protobuf.Mixin mixins = 6;</code>
     * @return \Google\Protobuf\Internal\RepeatedField
     */
    public function getMixins()
    {
        return $this->mixins;
    }

    /**
     * Included interfaces. See [Mixin][].
     *
     * Generated from protobuf field <code>repeated .google.protobuf.Mixin mixins = 6;</code>
     * @param \Google\Protobuf\Mixin[]|\Google\Protobuf\Internal\RepeatedField $var
     * @return $this
     */
    public function setMixins($var)
    {
        $arr = GPBUtil::checkRepeatedField($var, \Google\Protobuf\Internal\GPBType::MESSAGE, \Google\Protobuf\Mixin::class);
        $this->mixins = $arr;

        return $this;
    }

    /**
     * The source syntax of the service.
     *
     * Generated from protobuf field <code>.google.protobuf.Syntax syntax = 7;</code>
     * @return int
     */
    public function getSyntax()
    {
        return $this->syntax;
    }

    /**
     * The source syntax of the service.
     *
     * Generated from protobuf field <code>.google.protobuf.Syntax syntax = 7;</code>
     * @param int $var
     * @return $this
     */
    public function setSyntax($var)
    {
        GPBUtil::checkEnum($var, \Google\Protobuf\Syntax::class);
        $this->syntax = $var;

        return $this;
    }

}

>>>>>>> BRANCH (62c402 Add 'java/lite/target' to .gitignore (#8439))
