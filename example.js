/**
 * New node file
 */
//var https = require('https');
var request = require('request')
var crypto = require('crypto');
var fs = require('fs');
var credentials = {};
var api_path = '/v1/';
var BITFINEX = 'https://api.bitfinex.com';

var credentials = [];
credentials['api_key'] = '';
credentials['api_secret'] = '';

/**
 * prepare_payload Args: payload as JS object, api_key as string Returns:
 * prepared payload for the header encoded
 */


function prepare_payload(payload, secret) {
    var payloadJson = JSON.stringify(payload);
    return new Buffer(payloadJson).toString('base64');
}


/**
 * prepare_header Args: api_key as string, payload for call as JS Object payload
 * as javascript object, Returns: header for HTTPS call
 */


function prepare_headers(api_key, api_secret, payload) {
    var preparedPayload = prepare_payload(payload);
    var headers = {
        "X-BFX-APIKEY": api_key,
        "X-BFX-PAYLOAD": preparedPayload,
        "X-BFX-SIGNATURE": crypto.createHmac('sha384', api_secret).update(preparedPayload).digest('hex')
    };
    return headers;
}


/**
 * perform_callout Args: url of call as string, http call type as string,
 * payload of call for encoding
 */


function perform_callout_authenticated(url, calloutType, payload, callback) {
    payload.request = api_path + url;
    payload.nonce = Date.now().toString();
    //console.log(Date() + ': nonce '+payload.nonce);
    try {
        request({
            method: calloutType,
            url: BITFINEX + api_path + url,
            headers: prepare_headers(credentials.api_key, credentials.api_secret, payload)
        }, function(error, response, body) {
            try {
                callback(JSON.parse(body));
            } catch (e) {
                return callback(body);
            }
        });
    } catch (error) {
        console.log(Date() + ': perform_callout_authenticated error: ' + error);
        throw error;
    }
}


/**
 * perform_callout Args: url of call as string, http call type as string,
 * payload of call for encoding
 */


function perform_callout(url, calloutType, payload, callback) {
    try {
        request({
            method: calloutType,
            url: BITFINEX + api_path + url,
        }, function(error, response, body) {
            callback(JSON.parse(body));
        });
    } catch (error) {
        throw error;
    }
}


/**
 * get_ticker args: ticker of desired symbol's ticker, returns ticker as json
 * string
 */
exports.get_ticker = function(symbol, callback) {
    payload = {};
    perform_callout('ticker/' + symbol, 'GET', payload, function(response) {
        //callback(response);
        callback(response);
    });
};


/**
 *
 */
exports.get_today = function(symbol, callback) {
    payload = {};
    perform_callout('today/' + symbol, 'GET', payload, function(response) {
        callback(response);
    });
};


/**
 *
 */
exports.get_book = function(symbol, callback) {
    payload = {};
    perform_callout('book/' + symbol, 'GET', payload, function(response) {
        callback(response);
    });
};


/**
 *
 */
exports.get_trades = function(symbol, callback) {
    payload = {};
    perform_callout('trades/' + symbol, 'GET', payload, function(response) {
        callback(response);
    });
};


/**
 *
 */
exports.get_symbols = function(callback) {
    payload = {};
    perform_callout('symbols/', 'GET', payload, function(response) {
        callback(response);
    });
};


/**
 * order_new args: symbol (string), decimalAmountAsString (string),
 * decimalPriceAsString (string), exchange (string), side (string), orderType
 * (string) returns: order_id (string)
 */
exports.order_new = function(symbol, decimalAmountAsString, decimalPriceAsString, exchange, side, orderType, callback) {
    var payload = {
        'symbol': symbol,
        'amount': decimalAmountAsString,
        'price': decimalPriceAsString,
        'exchange': exchange,
        'side': side,
        'type': orderType
    };


    perform_callout_authenticated('order/new', 'POST', payload, function(response) {
        callback(response);
    });
};


/**
 * order_cancel args: order_id (int)
 * returns
 */
exports.order_cancel = function(order_id, callback) {
    var payload = {
        'order_id': order_id
    };
    perform_callout_authenticated('order/cancel', 'POST', payload, function(response) {
        callback(response);
    });
};


/**
 * order_status args: order_id (int)
 * returns
 */
exports.order_status = function(order_id, callback) {
    var payload = {
        'order_id': order_id
    };
    perform_callout_authenticated('order/cancel', 'GET', payload, function(response) {
        callback(response);
    });
};


/**
 * order_status args: order_id (int)
 * returns
 */
exports.get_orders = function(callback) {
    var payload = {};
    perform_callout_authenticated('orders', 'GET', payload, function(response) {
        callback(response);
    });
};


/**
 * get_positions
 */
exports.get_positions = function(callback) {
    var payload = {};
    perform_callout_authenticated('positions', 'GET', payload, function(response) {
        callback(response);
    });
};


/**
 * order_status args: order_id (int)
 * returns
 */
exports.get_balances = function(callback) {
    var payload = {};
    perform_callout_authenticated('balances', 'GET', payload, function(response) {
        callback(response);
    });
};


/**
 * cancel_orders
 */
exports.cancel_orders = function(ids_of_orders_to_cancel, callback) {
    var payload = {
        'order_ids': ids_of_orders_to_cancel
    };


    perform_callout_authenticated('order/cancel/multi', 'POST', payload, function(response) {
        callback(response);
    });
};


/**
 *  get_my_trades
 *  arguments:
 *  symbol
 *  timestamp (begging of trade history to-be-returned)
 *      remember UNIX timestamp seconds (Date().getTime() / 1000)
 *  limit_trades
 *  returns:
 *  trade history
 */
exports.get_my_trades = function(symbol, timestamp, limit_trades, callback) {
    var payload = {
        'symbol': symbol,
        'timestamp': timestamp,
        'limit_trades': limit_trades
    }


    perform_callout_authenticated('mytrades', 'GET', payload, function(response) {
        callback(response);
    });
}


/** 
 *  get_lends
 */
exports.get_lends = function(currency, timestamp, limit_lends, callback) {
    var payload = {
        'timestamp': timestamp,
        'limit_lends': limit_lends
    }


    perform_callout('lends/' + currency, 'GET', 'payload', function(response) {
        callback(response);
    });
}


/**
 *  place_new_orders
 */
exports.place_new_orders = function(orders, callback) {
    var payload = {
        'orders': orders
    };
    perform_callout_authenticated('order/new/multi', 'POST', payload, function(response) {
        callback(response);
    });
};