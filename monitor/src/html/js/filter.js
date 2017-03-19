var moduleName = "strategy.filters";
angular.module(moduleName, []).filter("direction",
    function() {
        return function(val) {
            var s = null;
            switch (val[1]) {
            case 1:
                switch (val[0]) {
                case 1:
                    s = "买/开";
                    break;
                case 2:
                    s = "卖/开"
                }
                break;
            case 2:
            case 3:
                switch (val[0]) {
                case 1:
                    s = "卖/平";
                    break;
                case 2:
                    s = "买/平"
                }
                break;
            case 4:
                switch (val[0]) {
                case 1:
                    s = "卖/平昨";
                    break;
                case 2:
                    s = "买/平昨"
                }
                break;
            default:
                switch (val[0]) {
                case 1:
                    s = "买/多";
                    break;
                case 2:
                    s = "卖/空"
                }
            }
            return s
        }
    }).filter("position_volume",
    function() {
        return function(val) {
            return val[1] ? val[0] + "/" + val[1] : val[0] + "/0"
        }
    }).filter("price",
    function() {
        return function(val) {
            return 0 === val[1] ? "number" == typeof val[0] ? val[0].toFixed(2) : val[0] : "市价"
        }
    }).filter("unfinish_order",
    function() {
        return function(val) {
            var s = null;
            if (1 == val[1]) switch (val[0]) {
            case 1:
                s = "买";
                break;
            case 2:
                s = "卖"
            } else if (2 === val[1] || 3 === val[1] || 4 === val[1]) switch (val[0]) {
            case 1:
                s = "卖";
                break;
            case 2:
                s = "买"
            } else switch (val[0]) {
            case 1:
                s = "买";
                break;
            case 2:
                s = "卖"
            }
            return s += " ",
            s += val[2]
        }
    }).filter("side",
    function() {
        return function(val) {
            var s = null;
            switch (val) {
            case 1:
                s = "买/多";
                break;
            case 2:
                s = "卖/空"
            }
            return s
        }
    }).filter("exchange",
    function() {
        return function(val) {
            var ex_map = {
                SHSE: "上交所",
                SZSE: "深交所",
                CFFEX: "中金所",
                SHFE: "上期所",
                DCE: "大商所",
                CZCE: "郑商所"
            };
            return ex_map[val]
        }
    }).filter("order_type",
    function() {
        return function(val) {
            var ot_map = {
                1 : "市价单",
                0 : "限价单"
            };
            return ot_map[val]
        }
    }).filter("pos_effect",
    function() {
        return function(val) {
            var pe_map = {
                1 : "开",
                2 : "平",
                3 : "平昨"
            };
            return pe_map[val]
        }
    }).filter("status",
    function() {
        return function(val) {
            var status_map = {
                1 : "已报",
                2 : "部成",
                3 : "全成",
                4 : "收市",
                5 : "已撤",
                6 : "待撤",
                7 : "停止",
                8 : "拒绝",
                9 : "挂起",
                10 : "待报",
                11 : "折算",
                12 : "过期",
                13 : "竞价",
                14 : "待改"
            };
            return status_map[val]
        }
    }).filter("indicator",
    function() {
        return function(val) {
            var indicator_map = {
                nav: "权益",
                pnl: "赢亏",
                profit_ratio: "绝对收益",
                annual_return: "年化收益",
                win_ratio: "胜率",
                max_drawdown: "最大回撤",
                risk_ratio: "风险比例",
                sharp_ratio: "夏普率",
                trade_count: "交易次数",
                win_count: "赢利次数",
                lose_count: "亏损次数",
                max_profit: "最大赢利",
                min_profit: "最大亏损",
                max_single_trade_profit: "单笔最大赢利",
                min_single_trade_profit: "单笔最大亏损",
                daily_max_single_trade_profit: "单日最大赢利",
                daily_min_single_trade_profit: "单日最大亏损",
                max_position_value: "最大持仓",
                min_position_value: "最小持仓",
                daily_pnl: "日赢亏",
                daily_return: "日收益"
            };
            return indicator_map[val]
        }
    }).filter("strategy_category",
    function() {
        return function(val) {
            var category_map = {
                1 : "人工喊单",
                2 : "量化交易",
                3 : "投资组合"
            };
            return category_map[val]
        }
    }).filter("strategy_style",
    function() {
        return function(val) {
            var style_map = {
                1 : "稳健",
                2 : "进取",
                3 : "激进"
            };
            return style_map[val]
        }
    }).filter("strategy_status",
    function() {
        return function(val) {
            var stat_map = {
                1 : "已连接",
                2 : "已断开",
                3 : "错误"
            };
            if (void 0 !== val && val.hasOwnProperty("state")) {
                var tip = stat_map[val.state];
                return 3 == val.state && (tip = tip + ": " + val.err_msg),
                tip
            }
            return "-"
        }
    }).filter("percentage",
    function() {
        return function(val, fix) {
            return void 0 !== val && null !== val ? "number" == typeof fix ? (100 * val).toFixed(fix) + "%": (100 * val).toFixed(2) + "%": "-"
        }
    }).filter("available",
    function() {
        return function(val) {
            return void 0 === val || null === val ? "-": val
        }
    }).filter("cny",
    function() {
        return function(val) {
            return "-" === val ? val: val + " 元"
        }
    }).filter("numlimit",
    function() {
        return function(val, lim) {
            return val > lim ? lim + "+": val
        }
    }).filter("str_len",
    function() {
        return function(val, lim) {
            return val ? val.length <= lim ? val: val.substr(0, lim) + "...": val
        }
    }).filter("orderObjectBy",
    function() {
        return function(items, field, reverse) {
            var filtered = [];
            return angular.forEach(items,
            function(item) {
                filtered.push(item)
            }),
            filtered.sort(function(a, b) {
                return a[field] > b[field] ? 1 : -1
            }),
            reverse && filtered.reverse(),
            filtered
        }
    }).filter("price_type",
    function() {
        return function(val) {
            var type_map = {
                0 : "不复权",
                1 : "前复权"
            };
            return type_map[val]
        }
    }).filter("execrpt_type",
    function() {
        return function(val) {
            var type_map = {
                1 : "已报",
                4 : "当日已完成",
                5 : "已撤销",
                6 : "待撤销",
                7 : "已停止",
                8 : "已拒绝",
                9 : "挂起",
                10 : "待报",
                11 : "已计算",
                12 : "过期",
                13 : "重置",
                14 : "待修改",
                15 : "成交",
                16 : "成交更正",
                17 : "撤销",
                18 : "委托状态",
                19 : "撤单被拒绝"
            };
            return type_map[val]
        }
    }).filter('account_status', function () {
        return function (val) {
            var stat_map = {
                1: '已连接',
                2: '已登录',
                3: '已断开',
                4: '错误'
            },
            tip = stat_map[val.state];
            return 4 == val.state && (tip = tip + ': ' + val.err_msg),
            tip
        }
    })
;