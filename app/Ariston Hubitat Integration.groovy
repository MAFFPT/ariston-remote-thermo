/*
    This integration is largely based on the work of @chomupashchuk (https://github.com/chomupashchuk/ariston-remotethermo-api)
    and is based on version 1.0.52, available on GitHub on april, 17 2022.
    
    API automatically filters out requests based on this list to reduce amount of unique requests towards the server
    and thus reduce a period to update the data.
    
     from the allowed list of sensors:
        - 'account_ch_gas' - gas use for CH.
        - 'account_ch_electricity' - electricity use for CH.
        - 'account_dhw_gas' - gas use for DHW.
        - 'account_dhw_electricity' - electricity use for DHW.
        - 'ch_antifreeze_temperature' - atifreeze temperature for CH.
        - 'ch_mode' - CH mode.
        - 'ch_set_temperature' - CH temperature set as a target.
        - 'ch_comfort_temperature' - CH comfort temperature.
        - 'ch_economy_temperature' - CH economy temperature.
        - 'ch_detected_temperature' - CH detected room temperature.
        - 'ch_program' - CH program information.
        - 'ch_pilot' - CH pilot status.
        - 'ch_auto_function' - CH auto function.
        - 'ch_flame' - CH flame.
        - 'ch_fixed_temperature' - CH fixed temperature.
        - 'ch_flow_temperature' - CH flow setpoint temperature.
        - 'ch_water_temperature' - CH water temperature.
        - 'cooling_last_24h' - energy use for pump cooling in a day.
        - 'cooling_last_7d' - energy use for pump cooling in a week.
        - 'cooling_last_30d' - energy use for pump cooling in a month.
        - 'cooling_last_365d' - energy use for pump cooling in a year.
        - 'cooling_last_24h_list' - energy use for pump cooling in a day with periods.
        - 'cooling_last_7d_list' - energy use for pump cooling in a week with periods.
        - 'cooling_last_30d_list' - energy use for pump cooling in a month with periods.
        - 'cooling_last_365d_list' - energy use for pump cooling in a year with periods.
        - 'errors' - list of active errors.
        - 'errors_count' - number of active errors
        - 'dhw_comfort_function' - DHW comfort function.
        - 'dhw_mode' - DHW mode.
        - 'dhw_program' - DHW program information.
        - 'dhw_set_temperature' - DHW temperature set as a target.
        - 'dhw_storage_temperature' - DHW storeage probe temperature.
        - 'dhw_comfort_temperature' - DHW comfort temperature.
        - 'dhw_economy_temperature' - DHW economy temperature.
        - 'dhw_thermal_cleanse_function' - DHW thermal cleanse function.
        - 'dhw_thermal_cleanse_cycle' - DHW thermal cleanse cycle.
        - 'dhw_flame' - approximated DHW flame status.
        - 'heating_last_24h' - energy use for CH in a day.
        - 'heating_last_7d' - energy use for CH in a week.
        - 'heating_last_30d' - energy use for CH in a month.
        - 'heating_last_365d' - energy use for CH in a year.
        - 'heating_last_24h_list' - energy use for CH in a day with periods.
        - 'heating_last_7d_list' - energy use for CH in a week with periods.
        - 'heating_last_30d_list' - energy use for CH in a month with periods.
        - 'heating_last_365d_list' - energy use for CH in a year with periods.
        - 'mode' - general mode.
        - 'outside_temperature' - outside temperature.
        - 'pressure' - CH water pressure.
        - 'signal_strength' - signal strength.
        - 'water_last_24h' - energy use for DHW in a day.
        - 'water_last_7d' - energy use for DHW in a week.
        - 'water_last_30d' - energy use for DHW in a month.
        - 'water_last_365d' - energy use for DHW in a year.
        - 'water_last_24h_list' - energy use for DHW in a day with periods.
        - 'water_last_7d_list' - energy use for DHW in a week with periods.
        - 'water_last_30d_list' - energy use for DHW in a month with periods.
        - 'water_last_365d_list' - energy use for DHW in a year with periods.
        - 'units' - indicates if metric or imperial units to be used.
        - 'gas_type' - type of gas.
        - 'gas_cost' - gas cost.
        - 'electricity_cost' - electricity cost.
        - 'flame' - CH or DHW flame detcted.
        - 'heat_pump' - heating pump.
        - 'holiday_mode' - holiday mode.
        - 'internet_time' - internet time.
        - 'internet_weather' - internet weather.
        - API specific 'update' - API update is available.
        - API specific 'online_version' - API version online.
        
    'retries'       - number of retries to set the data;
    'polling'       - defines multiplication factor for waiting periods to get or set the data;
    'store_file'    - indicates if HTTP and internal data to be stored as files for troubleshooting purposes;
    'store_folder'  - folder to store HTTP and internal data to. If empty string is used, then current working directory is used
                      with a folder 'http_logs' within it.
    'units'         - 'metric' or 'imperial' or 'auto'.
                      Value 'auto' creates additional request towards the server and as a result increases period to update other sensors.
    'ch_and_dhw'    - indicates if CH and DHW heating can work at the same time (usually valve allows to use one);
    'dhw_unknown_as_on' - indicates if to assume 'dhw_flame' as being True if cannot be identified.
    'logging_level' - defines level of logging - allowed values [CRITICAL, ERROR, WARNING, INFO, DEBUG, NOTSET=(default)]
    'zones'         - specifies number of monitored zones. By default is 1. For senors, which depend on zones, new sensor values shall 
                      be set (ending with _zone_2 and _zone_3), otherwise they shall remain unset.
                      
*/

import groovy.transform.Field

@Field static _Version = "0.1.0"

@Field static _LEVEL_CRITICAL = "CRITICAL"
@Field static _LEVEL_ERROR = "ERROR"
@Field static _LEVEL_WARNING = "WARNING"
@Field static _LEVEL_INFO = "INFO"
@Field static _LEVEL_DEBUG = "DEBUG"
@Field static _LEVEL_NOTSET = "NOTSET"
@Field static _LOGGING_LEVELS = [
        _LEVEL_CRITICAL,
        _LEVEL_ERROR,
        _LEVEL_WARNING,
        _LEVEL_INFO,
        _LEVEL_DEBUG,
        _LEVEL_NOTSET
    ]

@Field static _ZONE_1 = '_zone_1'
@Field static _ZONE_2 = '_zone_2'
@Field static _ZONE_3 = '_zone_3'
@Field static _ZONE_ORDER = [_ZONE_1, _ZONE_2, _ZONE_3]
@Field static _ADD_ZONES_START = 2
@Field static _ADD_ZONES_STOP = 3

@Field static _PARAM_ACCOUNT_CH_GAS = "account_ch_gas"
@Field static     _PARAM_ACCOUNT_CH_ELECTRICITY = "account_ch_electricity"
@Field static     _PARAM_ACCOUNT_DHW_GAS = "account_dhw_gas"
@Field static     _PARAM_ACCOUNT_DHW_ELECTRICITY = "account_dhw_electricity"
@Field static     _PARAM_CH_ANTIFREEZE_TEMPERATURE = "ch_antifreeze_temperature"
@Field static     _PARAM_CH_MODE = "ch_mode"
@Field static     _PARAM_CH_SET_TEMPERATURE = "ch_set_temperature"
@Field static     _PARAM_CH_COMFORT_TEMPERATURE = "ch_comfort_temperature"
@Field static     _PARAM_CH_ECONOMY_TEMPERATURE = "ch_economy_temperature"
@Field static     _PARAM_CH_DETECTED_TEMPERATURE = "ch_detected_temperature"
@Field static     _PARAM_CH_PROGRAM = "ch_program"
@Field static     _PARAM_CH_WATER_TEMPERATURE = "ch_water_temperature"
@Field static     _PARAM_COOLING_LAST_24H = "cooling_last_24h"
@Field static     _PARAM_COOLING_LAST_7D = "cooling_last_7d"
@Field static    _PARAM_COOLING_LAST_30D = "cooling_last_30d"
@Field static    _PARAM_COOLING_LAST_365D = "cooling_last_365d"
@Field static    _PARAM_COOLING_LAST_24H_LIST = "cooling_last_24h_list"
@Field static     _PARAM_COOLING_LAST_7D_LIST = "cooling_last_7d_list"
    _PARAM_COOLING_LAST_30D_LIST = "cooling_last_30d_list"
    _PARAM_COOLING_LAST_365D_LIST = "cooling_last_365d_list"
    _PARAM_COOLING_TODAY = "cooling_today"
    _PARAM_ERRORS = "errors"
    _PARAM_ERRORS_COUNT = "errors_count"
    _PARAM_DHW_COMFORT_FUNCTION = "dhw_comfort_function"
    _PARAM_DHW_MODE = "dhw_mode"
    _PARAM_DHW_PROGRAM = "dhw_program"
    _PARAM_DHW_SET_TEMPERATURE = "dhw_set_temperature"
    _PARAM_DHW_STORAGE_TEMPERATURE = "dhw_storage_temperature"
    _PARAM_DHW_COMFORT_TEMPERATURE = "dhw_comfort_temperature"
    _PARAM_DHW_ECONOMY_TEMPERATURE = "dhw_economy_temperature"
    _PARAM_HEATING_LAST_24H = "heating_last_24h"
    _PARAM_HEATING_LAST_7D = "heating_last_7d"
    _PARAM_HEATING_LAST_30D = "heating_last_30d"
    _PARAM_HEATING_LAST_365D = "heating_last_365d"
    _PARAM_HEATING_LAST_24H_LIST = "heating_last_24h_list"
    _PARAM_HEATING_LAST_7D_LIST = "heating_last_7d_list"
    _PARAM_HEATING_LAST_30D_LIST = "heating_last_30d_list"
    _PARAM_HEATING_LAST_365D_LIST = "heating_last_365d_list"
    _PARAM_HEATING_TODAY = "heating_today"
    _PARAM_MODE = "mode"
    _PARAM_OUTSIDE_TEMPERATURE = "outside_temperature"
    _PARAM_SIGNAL_STRENGTH = "signal_strength"
    _PARAM_WATER_LAST_24H = "water_last_24h"
    _PARAM_WATER_LAST_7D = "water_last_7d"
    _PARAM_WATER_LAST_30D = "water_last_30d"
    _PARAM_WATER_LAST_365D = "water_last_365d"
    _PARAM_WATER_LAST_24H_LIST = "water_last_24h_list"
    _PARAM_WATER_LAST_7D_LIST = "water_last_7d_list"
    _PARAM_WATER_LAST_30D_LIST = "water_last_30d_list"
    _PARAM_WATER_LAST_365D_LIST = "water_last_365d_list"
    _PARAM_WATER_TODAY = "water_today"
    _PARAM_UNITS = "units"
    _PARAM_THERMAL_CLEANSE_CYCLE = "dhw_thermal_cleanse_cycle"
    _PARAM_GAS_TYPE = "gas_type"
    _PARAM_GAS_COST = "gas_cost"
    _PARAM_ELECTRICITY_COST = "electricity_cost"
    _PARAM_CH_AUTO_FUNCTION = "ch_auto_function"
    _PARAM_CH_FLAME = "ch_flame"
    _PARAM_DHW_FLAME = "dhw_flame"
    _PARAM_FLAME = "flame"
    _PARAM_HEAT_PUMP = "heat_pump"
    _PARAM_HOLIDAY_MODE = "holiday_mode"
    _PARAM_INTERNET_TIME = "internet_time"
    _PARAM_INTERNET_WEATHER = "internet_weather"
    _PARAM_THERMAL_CLEANSE_FUNCTION = "dhw_thermal_cleanse_function"
    _PARAM_CH_PILOT = "ch_pilot"
    _PARAM_UPDATE = "update"
    _PARAM_ONLINE_VERSION = "online_version"
    _PARAM_PRESSURE = "pressure"
    _PARAM_CH_FLOW_TEMP = 'ch_flow_temperature'
    _PARAM_CH_FIXED_TEMP = 'ch_fixed_temperature'

