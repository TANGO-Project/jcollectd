package org.collectd.common.protocol;

/**
 * Constants from collectd/src/network.h
 * @see <a href="http://collectd.org/wiki/index.php/Binary_protocol">Collect Binary Protocol</a>
 */
interface INetwork {

    /**
     * Host - The name of the host to associate with subsequent data values
     *
     * @type String
     */
    public static final int TYPE_HOST = 0x0000;

    /**
     * Time - The timestamp to associate with subsequent data values, unix time format (seconds since epoch)
     *
     * @type Numeric
     */
    public static final int TYPE_TIME = 0x0001;

    /**
     * Plugin - The plugin name to associate with subsequent data values, e.g. "cpu"
     *
     * @type String
     */
    public static final int TYPE_PLUGIN = 0x0002;

    /**
     * Plugin instance - The plugin instance name to associate with subsequent data values, e.g. "1"
     *
     * @type String
     */
    public static final int TYPE_PLUGIN_INSTANCE = 0x0003;

    /**
     * Type - The type name to associate with subsequent data values, e.g. "cpu"
     *
     * @type String
     */
    public static final int TYPE_TYPE = 0x0004;

    /**
     * Type instance - The type instance name to associate with subsequent data values, e.g. "idle"
     *
     * @type String
     */
    public static final int TYPE_TYPE_INSTANCE = 0x0005;

    /**
     * Values - Data values
     *
     * @type ValueList/other
     */
    public static final int TYPE_VALUES = 0x0006;

    /**
     * Interval - used to set the "step" when creating new RRDs unless rrdtool plugin forces StepSize. Also used to detect values that have timed out.
     *
     * @type Numeric
     */
    public static final int TYPE_INTERVAL = 0x0007;

    /**
     * Timestamp - The timestamp to associate with subsequent data values. Time is defined in 2–30 seconds since epoch. New in Version 5.0.
     *
     * @type Numeric
     * @see <a href="http://collectd.org/wiki/index.php/High_resolution_time_format">High resolution time format</a>
     */
    public static final int TYPE_TIME_HIRES = 0x0008;

    /**
     * Interval - The interval in which subsequent data values are collected. The interval is given in 2–30 seconds. New in Version 5.0.
     *
     * @type Numeric
     * @see <a href="http://collectd.org/wiki/index.php/High_resolution_time_format">High resolution time format</a>
     */
    public static final int TYPE_INTERVAL_HIRES = 0x0009;

    /**
     * Notification Message
     *
     * @type String
     */
    public static final int TYPE_MESSAGE = 0x0100;

    /**
     * Notification Sevirity
     *
     * @type Numeric
     */
    public static final int TYPE_SEVERITY = 0x0101;

    /**
     * Signature (HMAC-SHA-256)
     *
     * @type other
     */
    public static final int TYPE_SIG = 0x0200;

    /**
     * Encryption (AES-256/OFB/SHA-1)
     *
     * @type other
     */
    public static final int TYPE_ENC = 0x0210;

}
