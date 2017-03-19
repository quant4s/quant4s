'use strict';
var appInfo = {
    host: 'http://localhost:8888'
};
var color = {
    blue: '#348fe2',
    blueLight: '#5da5e8',
    blueDark: '#1993E4',
    aqua: '#49b6d6',
    aquaLight: '#6dc5de',
    aquaDark: '#3a92ab',
    green: '#00acac',
    greenLight: '#1EB414',
    greenDark: '#008a8a',
    orange: '#f59c1a',
    orangeLight: '#f7b048',
    orangeDark: '#c47d15',
    dark: '#2d353c',
    grey: '#b6c2c9',
    purple: '#727cb6',
    purpleLight: '#8e96c5',
    purpleDark: '#5b6392',
    red: '#ff5b57',
    redDark: '#AE0000',
    golden: '#FFD700'
};

document.title = "招财终端 V1.0";

var app = angular.module('quant4s', ['ui.router', 'ui.bootstrap','auth', 'util', 'gmsdk','custom-flot','strategy.filters']);
app.constant('color', color)
    .constant('appInfo', appInfo);



// ============================================= 控制器开始 =============================================
app.controller('NavbarCtrl', ['$scope', function($scope) {
     }])
    .controller('SidebarCtrl', ['$scope','$log', function($scope, $log) {
        $log.log('[SidebarCtrl]构造函数。');
        $scope.profile = {
            username:"Leo",
            out_of_date:"20170101",
        };

        $scope.sidebar_minify = function () {
            $scope.minified = !$scope.minified;
            $scope.minified ? angular.element('#page-container').addClass('page-sidebar-minified')  : angular.element('#page-container').removeClass('page-sidebar-minified');
            //$scope.$root.$broadcast('event:page-sidebar-minified', $scope.minified);
        };
    }])
    .controller('AboutCtrl', ['$scope', '$log', function($scope, $log) {
    }])
    .controller('LoginCtrl', ['$scope', '$window', '$uibModal', '$log', function($scope, $window, $uibModal, $log) {
        $log.log('[LoginCtrl]构造函数。');
        $scope.username = '',
        $scope.password = '';
        var modalInstance = null;
        $scope.login = function () {
            window.localStorage.setItem('username', $scope.username);
            //sso.login($scope.username, $scope.password)
        };
    }])
    .controller('BacktestListCtrl',['$scope', '$uibModal', 'BacktestService', '$log', function($scope, $uibModal, backtestService, $log) {
        $log.log('[BacktestListCtrl]构造函数。');
        $scope.strategies = []; //backtestService.getRawList();
        getStrategyListWithBacktest(),
        $scope.$on('event:new_backtest', function () {
            getStrategyListWithBacktest();
        });
        var currentStrategy = $scope.strategies.length ? $scope.strategies[0] : {
            backtests: []
        };

        function getStrategyListWithBacktest() {
            backtestService.getList().then(function (strategies) {
                $scope.strategies = strategies
            }).then(function () {
                highlightCurrent(currentStrategy)
            })
        }
        $scope.select = function (strategy) {
            $log.log("[BacktestListCtrl.select]选中一个策略，显示回测报告");
            currentStrategy.allSelected = false,
            currentStrategy.backtests && currentStrategy.backtests.forEach(function (bt) {
                return bt.selected = false
            }),
            strategy.backtests || this.toastr.error('后台错误', '查询' + strategy.name + '回测报告出错'),
            this.strategies.forEach(function (strategy) {
                strategy.is_active = false
            }),
            strategy.is_active = true,
            currentStrategy = strategy;
        };

    }])
    .controller('BacktestDetailCtrl',['$scope', '$log', function($scope, $log) {
        $log.log('[BacktestDetailCtrl]构造函数。');
    }])
    .controller('StrategyListCtrl', ['$scope', 'StrategyService', 'RiskService', '$log', function($scope, strategyService, riskService, $log) {
        //$scope.title = $scope.$state.includes('app.simtrade') ? '模拟交易' : '实盘交易';
        $log.log('[StrategyListCtrl]构造函数，获取所有策略。');
        $scope.strategies = strategyService.getStrategies();
        $scope.options = {
            series: {
                shadowSize: 0,
                color: color.golden,
                lines: {
                    show: !0,
                    fill: !1
                }
            },
            color: color.golden,
            grid: {
                show: !1,
                hoverable: !0
            }
        };
        $scope.change_risk_onoff = function (strategy) {
            riskService.changeRiskSwitch(strategy)
        };
        //$scope.strategies = strategyService.list($http, $scope);
    }])
    .controller('StrategyDetailCtrl',['$scope', '$stateParams', '$uibModal', 'StrategyService','$log', function($scope, $stateParams, $uibModal, strategyService, $log){
        $log.log('[StrategyDetailCtrl]构造函数。');
        $scope.row_id = $stateParams.strategy_id;
        $scope.row = strategyService.get($scope.row_id);

        angular.element('#page-container').addClass('page-with-two-sidebar');
        $scope.$on('$destroy', function () {
            angular.element('#page-container').removeClass('page-with-two-sidebar')
        });

        // 复制ID
        $scope.on_copied = function () {};

        // 切换Tab
        $scope.current_tab = 'positions';
        $scope.change_tab = function (tab) {
            $scope.current_tab = tab
        };

        // 权益曲线
        $scope.current_chart = 'today';
        $scope.change_chart = function (chart) {
            $scope.current_chart = chart
        };

        // 持仓调整
        $scope.position_io = function () {
            $uibModal.open({
                templateUrl: 'views2/strategy/setting.position.html',
                controller: 'SettingPositionCtrl',
                controllerAs: 'modal',
                backdrop: 'static',
                size: 'lg'
            });
        }
    }])
    .controller('StrategyUnfinishedOrdersCtrl',['$scope', '$uibModal', 'StrategyService','$log', function($scope, $uibModal, strategyService, $log){
        $log.log('[StrategyUnfinishedOrdersCtrl]构造函数。');
        $scope.strategy = $scope.$parent.row;
        $scope.displayedCollection = $scope.strategy.unfinished_orders;
//        $scope.unfinishedOrders = [].concat($scope.unfinished);
//        $scope.displayedCollection = $scope.unfinished;
        $scope.cancelOrder = function (order) {
            $scope.sec_id = [order.exchange,order.sec_id].join('.'),
            $scope.sec_name = order.sec_name,
            $scope.volume = order.volume - order.filled_volume;
            var modalInstance = $modal.open({
                templateUrl: 'views2/strategy/cancel.order.html',
                controller: 'CancelOrderCtrl',
                backdrop: 'static',
                resolve: {
                    order: function () {
                        return order
                    },
                    ok: function () {
                        return '撤单'
                    }
                }
            });
        };
    }])
    .controller('StrategyPositionsCtrl',['$scope', 'StrategyService','$log', function($scope, strategyService, $log){
        $log.log('[StrategyPositionsCtrl]构造函数。');
        $scope.strategy = $scope.$parent.row;
        $scope.displayedCollection = [].concat($scope.strategy.positions);

    }])
    .controller('StrategyOrdersCtrl',['$scope', 'StrategyService','$log', function($scope, strategyService, $log){
        $log.log('[StrategyOrdersCtrl]构造函数。');
        $scope.strategy = $scope.$parent.row,
        $scope.displayedCollection = $scope.strategy.orders;
    }])
    .controller('StrategyTransactionsCtrl',['$scope', 'StrategyService','$log', function($scope, strategyService, $log){
        $log.log('[StrategyTransactionsCtrl]构造函数。');
        $scope.strategy = $scope.$parent.row,
        $scope.displayedCollection = $scope.strategy.trans;
    }])
    .controller('StrategyIndicatorsCtrl',['$scope', '$stateParams', 'StrategyService','$log', function($scope, $stateParams, strategyService, $log){
        $log.log('[StrategyIndicatorsCtrl]构造函数。');
        function gen_indicator_array(inds) {
            var keys = Object.keys(inds),
            sorted = window.localStorage.getItem('gm_client_indicator_order'),
            indicators = [];
            if (sorted) {
                for (var sorted_key = sorted.split(','), i = 0; i < sorted_key.length; i++) {
                    var k = sorted_key[i];
                    if (inds[k]) {
                        indicators.push(inds[k]);
                        var idx = keys.indexOf(k);
                        idx > -1 && keys.splice(idx, 1)
                    }
                }
                keys.length && keys.forEach(function(k) {
                    indicators.push(inds[k])
                })
            } else keys.forEach(function(k) {
                indicators.push(inds[k])
            });
            return indicators
        }
        var strategy_id = $stateParams.strategy_id,
        strategy = strategyService.get(strategy_id);
        $scope.indicator_obj = strategy ? strategy.indicators: {},
        $scope.indicators = gen_indicator_array($scope.indicator_obj);
//        $scope.$watchCollection('indicator_obj',
//        function() {
//            $scope.indicators = gen_indicator_array($scope.indicator_obj)
//        }),
//        $scope.$on('event:change_indicator_order',
//        function() {
//            $scope.indicators = gen_indicator_array($scope.indicator_obj)
//        })
    }])
    .controller('StrategyPerfChartCtrl',['$scope', '$stateParams', 'StrategyService','$log', function($scope, $stateParams, strategyService, $log){
        $log.log('[StrategyPerfChartCtrl]构造函数。');
        var strategy_id = $stateParams.strategy_id,
        strategy = strategyService.get(strategy_id);
        $scope.long_trends =[],// strategy ? strategy.long_trends: [],
        Highcharts.setOptions({
            global: {
                useUTC: !1,
                timezoneOffset: -(new Date).getTimezoneOffset()
            }
        }),
        $scope.chartConfig = {
            options: {
                chart: {
                    backgroundColor: 'transparent',
                    zoomType: '',
                    animation: !1,
                    height: 229
                },
                navigator: {
                    enabled: !1
                },
                rangeSelector: {
                    enabled: !1
                },
                exporting: !1,
                legend: {
                    enabled: !1
                },
                title: {
                    text: null
                },
                xAxis: {
                    type: 'datetime',
                    ordinal: !0,
                    title: {
                        text: null
                    },
                    labels: {
                        enabled: !1
                    },
                    lineWidth: 0,
                    minorGridLineWidth: 0,
                    lineColor: 'transparent',
                    minorTickLength: 0,
                    tickLength: 0,
                    dateTimeLabelFormats: {
                        day: '%Y<br/>%m-%d',
                        month: '%Y-%m',
                        year: '%Y'
                    }
                },
                yAxis: {
                    title: {
                        text: null
                    },
                    labels: {
                        enabled: !1
                    },
                    lineWidth: 0,
                    minorGridLineWidth: 0,
                    lineColor: 'transparent',
                    minorTickLength: 0,
                    tickLength: 0
                },
                tooltip: {
                    xDateFormat: '%Y-%m-%d'
                },
                plotLines: {
                    tickAmount: 0
                },
                plotOptions: {
                    area: {
                        marker: {
                            enabled: !1
                        }
                    },
                    series: {
                        animation: !1
                    }
                },
                loading: !1,
                useHighStocks: !0
            },
            series: [{
                name: '策略权益',
                data: $scope.long_trends,
                type: 'area',
                tooltip: {
                    valueSuffix: '万元'
                },
                color: color.golden,
                fillColor: {
                    linearGradient: {
                        x1: 0,
                        y1: 0,
                        x2: 0,
                        y2: 1
                    },
                    stops: [[0, color.golden], [1, Highcharts.Color(color.golden).setOpacity(0).get('rgba')]]
                },
                threshold: null
            }]
        }
    }])
    .controller('StrategyExecRptsCtrl',['$scope', 'StrategyService','$log', function($scope, strategyService, $log){
        $log.log('[StrategyExecRptsCtrl]构造函数。');
        $scope.strategy = $scope.$parent.row,
        $scope.displayedCollection = $scope.strategy.execrpts;
    }])
    .controller('SettingPositionCtrl',['$scope', '$stateParams', 'StrategyService', 'RiskService', 'util.typeahead', 'gmsdk.td', 'gmsdk.md', '$timeout', '$log', function($scope, $stateParams, strategyService, riskService, typeahead, backend, md, $timeout, $log){
        $log.log('[SettingPositionCtrl]构造函数。');
        var modal=this;
        var strategy_id = $stateParams.strategy_id;
        var strategy=strategyService.get(strategy_id);
        modal.pos_display=make_st_pos_mirror(strategy);
        modal.getSymbolsTypeahead = typeahead.getSymbolSuggestions(md.symbolTypeAhead);
        modal.new_item={};
        modal.sides = riskService.sides;

        function make_st_pos_mirror(strategy){
            var mirror=[].concat(strategy.positions);
            mirror.forEach(function (pos) {
                pos.vwap = pos.vwap ? Math.round(100 * pos.vwap) / 100 : 0,
                pos.volume = pos.volume ? pos.volume : 0,
                pos.volume_today = pos.volume_today ? pos.volume_today : 0,
                pos.volume_input = pos.volume,
                pos.volume_today_input = pos.volume_today,
                pos.vwap_input = pos.vwap
            })
            return mirror;
        };

        function showThenHide(time) {
            var temp = modal.submit_status;
            $timeout(function () {
                modal.submit_status === temp && (modal.submit_status = null)
            }, time)
        }
        modal.selectSymbol = function(item) {
            // 检查有没有持仓
            for (var find_duplicate = false, i = 0; i < $scope.selected_symbols.length; i++) {
                find_duplicate = false;
            }
            if(!find_duplicate) {
                var symbol = item;
                //symbol.sub_type = angular.copy(CreateService.sub_types),
                $scope.selected_symbols.push(symbol);
            }
            //
            $scope.onSubTypeChange();
        };
        $scope.onSubTypeChange = function() {
            $log.log("[SettingPositionCtrl.onSubTypeChange]执行");
        }
//
//        make_st_pos_mirror = function(strategy) {
//            $log.log("[SettingPositionCtrl.make_st_pos_mirror]构建策略仓位");
//        };

        modal.add_item = function(item) {
            $log.log("[SettingPositionCtrl.add_item]增加一个仓位" + item.symbol.sec_id);
            for (var i = 0; i < modal.pos_display.length; i++) {
                var this_pos = modal.pos_display[i];
                if (this_pos.sec_id === item.symbol.sec_id && this_pos.exchange === item.symbol.exchange && this_pos.side === item.side) {
                    modal.submit_status = 'duplicated';
                    $log.log("发现重复");
                    return ;
                }

            }
            modal.pos_display.push({exchange:"SZSE",sec_id:"symbol",sec_name:"name",side:item.side,volume:0,volume_input:item.volume,volume_today:0,volume_today_input:item.volume_today,vwap:item.vwap,vwap_input:item.vwap});
            modal.new_item = {};
        };

        modal.position_io = function () {
            modal.submit_status = null;
            var pos_to_io = {
                data: modal.pos_display.map(function (pos) {
                    return {
                      exchange: pos.exchange,
                      sec_id: pos.sec_id,
                      side: pos.side,
                      strategy_id: strategy_id,
                      volume: pos.volume_input - pos.volume,
                      volume_today: pos.volume_today_input - pos.volume_today,
                      vwap: pos.vwap_input
                    }
                })
            };
            backend.setPositions(strategy_id, pos_to_io)
            .success(function () {
                $log.log("成功保存仓位");
                strategyService.fetchPositions(strategy);
                modal.submit_status = 'success';
                showThenHide(4000)
            }).error(function () {
                $log.log("保存仓位失败");
                modal.submit_status = 'error',
                showThenHide(4000)
            });
            strategyService.fetchPositions(strategy);
        };

        modal.submit = function() {
            $log.log("[SettingPositionCtrl.submit]保存");
            modal.position_io();
//            modal.submit_status = null;
//            var has_error = false;
//            modal.pos_display.forEach(function (pos) {
//                pos.volume_input < pos.volume_today_input ? (pos.volume_error = true, has_error = true)  : pos.volume_error = false
//            }),
//            has_error ? modal.submit_status = 'volume_input_error' : modal.position_io()
        };
    }])
    .controller('StrategySidebarCtrl',['$scope', '$stateParams', '$uibModal', 'StrategyService','RiskService', '$log', function($scope, $stateParams, $uibModal, strategyService, riskService, $log){
        $log.log('[StrategySidebarCtrl]构造函数。');
        var strategy_id = $stateParams.strategy_id;
        $scope.strategy = strategyService.get(strategy_id);
        $scope.edit_strategy = function (target) {
            var modalInstance = $uibModal.open({
                templateUrl: 'views2/strategy/strategy.setting.html',
                controller: 'StrategySettingCtrl',
                controllerAs: 'modal',
                backdrop: 'static',
                resolve: {
                    strategy_id: function () {
                        return strategy_id
                    },
                    title: function () {
                        return target
                    }
                }
            });
            modalInstance.result.then(function (data) {
            angular.extend($scope.strategy.base, data),
            StrategyService.fetch_positions($scope.strategy)
            }, function (reason) {
            StrategyService.fetch_positions($scope.strategy),
            $scope.reason = reason
            })
        };

        $scope.risk_config = function() {
            var modalInstance = $uibModal.open({
                backdrop: 'static',
                templateUrl: 'views2/risk/risk.edit.html',
                controller: 'RiskEditCtrl',
                controllerAs: 'modal',
                size: 'lg',
                resolve: {
                    strategy_id: function () {
                        return strategy_id
                    }
                }
            });
        };

        $scope.change_risk_onoff = function(strategy) {
            riskService.changeRiskSwitch(strategy);
        };
    }])
    .controller('RiskEditCtrl',['$scope', '$stateParams', 'RiskService', '$log', function($scope, $stateParams, riskService, $log){
        $log.log('[RiskEditCtrl]构造函数。');
        var strategy_id = $stateParams.strategy_id;
    }])
    .controller('SymbolCtrl',['$scope', '$uibModal', 'util.typeahead','gmsdk.md','$log', function($scope, $uibModal, typeahead, md, $log){
        $log.log('[SymbolCtrl]构造函数。');
        function handleTypeaheadSelect(evt, selectedItem) {
            $uibModal.open({
                templateUrl: 'views2/strategy/strategy.placeorder.html',
                controller: 'PlaceOrderCtrl',
                backdrop: 'static',
                resolve: {
                    instrument: function () {
                        return selectedItem
                    },
                    order: function () {
                        return 1 === selectedItem.sec_type ? {
                            side: 1,
                            position_effect: 1,
                            volume: 100
                        } : {
                            side: 1,
                            position_effect: 1,
                            volume: 1
                        }
                    },
                    strategy: function () {
                      return strategy
                    }
                }
            })
        }

        var strategy = $scope.$parent.row,
        targetInput = $('#symbol-input'),
        dataSets = typeahead.getDataSets(md.symbolTypeAhead, 'is_active', 1),
        enterPressHandler = typeahead.getFirstMapSymbol(md.symbolTypeAhead, function (symbol) {
            $log.log('[SymbolCtrl.enterPressHandler]点击下单' + symbol);
            handleTypeaheadSelect(null, symbol),
            targetInput.val(symbol.sec_name || '')
        }, function () {
            targetInput.typeahead('close'),
            targetInput.blur()
        });
        setTimeout(function () {
//            var $__1;
//            ($__1 = targetInput).typeahead.apply($__1, $traceurRuntime.spread([typeahead.options], dataSets)).keypress(enterPressHandler)
        }, 0);
        $scope.handlePlaceOrderClick = function() {
            $log.log('[SymbolCtrl.handlePlaceOrderClick]点击下单');
            var evt = {};
            evt.which = 13,
            evt.target = {
                value: targetInput.val()
            };
            enterPressHandler(evt);
        };
    }])
    .controller('AccListCtrl',['$scope', '$state', '$http', '$uibModal', 'AccountService','$log', function($scope, $state, $http, $uibModal, accountService, $log) {
        $log.log('[AccListCtrl]构造函数。');
        $scope.accounts = accountService.getAccounts();
        $scope.displayedCollection = $scope.accounts;
        $scope.acc = {};

        $scope.type = function (id) {
            return accountService.account_type(id)
        };
        $scope.isConnected = function (acc) {
            return accountService.isLoggedIn(acc)
        };

        $scope.click_account = function(acc) {
            var dlg = null;
            if(acc.status.state == 2) {
                dlg = {
                          templateUrl:"views2/account/connect.confirm.html",
                          controller:"AccConnectCtrl",
                          backdrop:"static",
                          resolve:{
                              acc: function(){
                                  return acc;
                              }
                          },
                      };
            } else {
                dlg = {
                          templateUrl:"views2/account/disconnect.confirm.html",
                          controller:"AccDisconnectCtrl",
                          backdrop:"static",
                          resolve:{
                              acc: function(){
                                  return acc;
                              }
                          },
                      };
            }
            var modalInstance= $uibModal.open(dlg);
        };

        $scope.addItem = function() {
            $scope.acc = { };
             $scope.open();
        };

        $scope.editItem = function(account) {
            $scope.acc = angular.copy(account),
            $scope.open('修改账户');
        };

        $scope.remove_confirm = function (row) {
            var modalInstance = $uibModal.open({
                templateUrl: 'views2/account/remove.confirm.html',
                controller: 'AccRemoveCtrl',
                controllerAs: 'modal',
                backdrop: 'static',
                resolve: {
                    acc: function () {
                        return row
                    }
                }
            });
            modalInstance.result.then(function (acc) {
                $scope.acc = {};
                $scope.displayedCollection = accountService.fetchAccounts();
            }, function () {
            });
        };

        $scope.open = function (customTitle, size) {
            var title = customTitle || '新增账户';
            var modalInstance = $uibModal.open({
                templateUrl: 'views2/account/setting.html',
                controller: 'AccEditCtrl',
                backdrop: 'static',
                size: size,
                resolve: {
                    acc: function () {
                      return $scope.acc
                    },
                    title: function () {
                      return title
                    }
                }
            });
            modalInstance.result.then(function (acc) {
                $scope.acc = {};
                $scope.displayedCollection = accountService.fetchAccounts();
            }, function () {
            });
        };
    }])
    .controller('StrategySettingCtrl',['$scope', '$uibModalInstance', 'strategy_id', 'title', 'StrategyService', 'gmsdk.td', '$log', function($scope, $uibModalInstance, strategy_id, title, strategyService, backend, $log){
        $log.log('[StrategySettingCtrl]构造函数。');
        $scope.title = title,
        $scope.strategy = strategyService.get(strategy_id),
        $scope.cash_inout_dir = [
        {
            id: 1,
            name: '转入'
        },
        {
            id: 2,
            name: '转出'
        }
        ]
        $scope.title_map = {
            base_info: '修改基本信息',
            account: '修改关联账户',
            sync: '同步账户',
            cash_io: '资金出入'
        };

        function cash_io() {
            $scope.cash_io_error = !1;
            var sign = 1;
            2 === modal.cash_io_flag && (sign = - 1),
            $scope.saving = !0,
            $scope.cash_save_succeed = !1,
            backend.setCashInout(modal.wizard.strategy_id, sign * modal.cash_inout).success(function () {
                $scope.saving = !1,
                $scope.cash_save_succeed = !0,
                $scope.get_cash()
            }).error(function () {
                $scope.cash_io_error = !0,
                $scope.errors = '资金出入操作失败！',
                $scope.saving = !1,
                $scope.cash_save_succeed = !1
            })
        }

        $scope.ok = function() {
        }
    }])
    .controller('AccEditCtrl', ['$scope', '$uibModalInstance', 'acc', 'title', 'AccountService', '$log', function($scope, $uibModalInstance, acc, title, accountService, $log) {
        $log.log('[AccEditCtrl]构造函数。');
        $scope.brokerChannelTypes = accountService.getBrokerChannelTypes();
        var addMode = '新增账户' === title;
        $scope.acc = acc;
        $scope.title = title;

        $scope.onChannelSelect = function() {
        };

        $scope.ok = function () {
            addMode ? accountService.insert($scope.acc).then(function() {
                $uibModalInstance.close($scope.acc);
            }, function() {
                $scope.errors = '新增账户失败!'
            }) : accountService.update($scope.acc).then(function() {
                $log.log("修改账户");
                 $uibModalInstance.close($scope.acc);
             }, function() {
                 $scope.errors = '修改账户失败!'
             });

//            addMode && uploadChange();
//            var channel_type = parseInt($scope.acc.broker_channel_id);
//            $scope.acc.broker_channel_id = channel_type,
//            $scope.acc.account_type = channel_type,
//            $scope.acc.account_type > 3 && ($scope.acc.account_type = 3),
//            $scope.acc.account_id ? accountService.update($scope.acc).success(function () {
//                $modalInstance.close($scope.acc)
//            }).error(function () {
//            $scope.errors = '修改账户失败!'
//            })  : ($scope.acc.permissible = !0, AccountService.insert($scope.acc).success(function (resp) {
//            var na = resp.data[0];
//            $scope.acc.account_id || (angular.extend($scope.acc, na), $scope.acc.new_created = !0),
//            $modalInstance.close($scope.acc)
//            }).error(function () {
//            $scope.errors = '增加账户失败!'
//            }))
        };
    }])
    .controller('AccRemoveCtrl', ['$scope', '$uibModalInstance', 'acc', 'AccountService', '$log', function($scope, $uibModalInstance, acc, accountService, $log) {
        $log.log('[AccRemoveCtrl]构造函数，Account Id: ' + acc.id + ", 账户名: " + acc.name);
        $scope.acc_id = acc.id;
        $scope.account = acc;

        $scope.ok = function () {
            var id = $scope.acc_id;
            accountService.remove(id).success(function () {
                $uibModalInstance.close($scope.account)
            }).error(function () {
                $scope.errors = '删除账户失败！'
            });
        };
    }])
    .controller('AccDisconnectCtrl', ['$scope', '$uibModalInstance', 'acc', 'AccountService', '$log', function($scope, $uibModalInstance, acc, accountService, $log) {
        $scope.account = acc;
        $scope.ok = function () {

        }
    }])
    .controller('AccConnectCtrl', ['$scope', '$uibModalInstance', 'acc', 'AccountService', '$log', function($scope, $uibModalInstance, acc, accountService, $log) {
        $log.log('[AccConnectCtrl]构造函数');
        $scope.account = acc;
        $scope.ok = function () {

        }
    }])
    .controller('AccInfoCtrl', ['$scope', '$http', '$stateParams', 'AccountService', '$log', function($scope, $http, $stateParams, accountService, $log) {
        $log.log('[AccInfoCtrl]构造函数');
    }])
    .controller('AnalysisListCtrl', ['$scope', '$http', '$stateParams', 'AccountService', '$log', function($scope, $http, $stateParams, accountService, $log) {
        $log.log('[AnalysisListCtrl]构造函数');
    }])
    .controller('AnalysisDetailCtrl', ['$scope', '$http', '$stateParams', 'AccountService', '$log', function($scope, $http, $stateParams, accountService, $log) {
        $log.log('[AnalysisDetailCtrl]构造函数');
    }])
     .controller('RiskLogCtrl', ['$scope', '$http', '$stateParams', '$log', function($scope, $http, $stateParams, $log) {
        $log.log('[RiskLogCtrl]构造函数');
     }])

;

// ============================================= 控制器结束 =============================================

// ============================================= 服务开始 =============================================

app.factory('StrategyService', ['$rootScope', '$q', 'gmsdk.td', '$log',function($rootScope, $q, backend, $log){
        $log.log("[StrategyService]构造函数。");
        var initCb = [];
        var inited = false;

        var list = [];
        init().then(function () {
            fireInitCallBack()
        }, function () {
        });

        function fireInitCallBack () {
            $log.log('[StrategyService.fireInitCallBack] 调用初始化函数');
            initCb.forEach(function (fun) {
                'function' == typeof fun && fun();
            });
        }
        function init() {
            $log.log("[StrategyService.init]从restful 读取所有的策略。");
            var deferred = $q.defer();
            backend.getStrategies().then(function (resp) {
                for(var _data = resp.data.data, l = Math.min(_data.length, 200),i = 0; l > i; i++) {
                    var x = _data[i],
                    t = add_strategy_base(x);
                    // 从t中获取持仓、委托等信息
                    fetch_positions(t);
                    fetch_orders(t, 20);
                    fetch_trans(t, 20);
                    fetch_indicators(t);
                    fetch_trends(t);
                    fetch_unfinishedOrders(t);
                }
                deferred.resolve();
            }, function () {
                deferred.reject()
            });
            return deferred.promise;
        }

        function add_strategy_base(jsonStr) {
            $log.log("[StrategyService.add_strategy_base]增加一个策略。");
            var t = {
                strategy_id: jsonStr.id,
                name: jsonStr.name,
                indicators: [],
                running_status: {
                    state: 2,
                },
                long_trends:[],
                trends:[],
                positions:[],
                orders:[],
                unfinished_orders:[],
                trans:[],
                risk_state: 3,
                risk_config: {
                    enable: backend.getRiskConfig(jsonStr.id),
                },
                connected_account: 1,
                accounts:[{
                    account_name: 'CTP',
                    status: {
                        state: 2,
                    },
                }, {
                     account_name: 'LTS',
                     status: {
                         state: 2,
                     },
               }],
            };
            list.push(t);

            $rootScope.$broadcast('event:add_strategy', {
                id: t.id,
            });

            return t;
        }
        function fetch_positions(t) {
            backend.getPositions(t.strategy_id).then(function (resp) {
                t.positions = [];
                var pos = [].concat(resp.data.data);
                pos.forEach(function(x){
                    t.positions.push(x);
                });
            }, function() {
                $log.log("[StrategyService.fetch_positions]获取持仓信息出错");
            });
        }
        function fetch_orders(t, count){
            var deferred = $q.defer();
            backend.getLastNOrder(t.strategy_id, count).then(function(resp) {
                $log.log("[StrategyService.fetch_orders]获取委托信息" + resp.data.data);
                t.orders = [];
                var _data = [].concat(resp.data.data);
                _data.forEach(function(o){
                    t.orders.push(o);
                });
                deferred.resolve(t.orders);
            }, function() {
                $log.log("[StrategyService.fetch_orders]获取委托信息出错");
                deferred.reject();
            });
            return deferred.promise;
        }
        function fetch_indicators(t){
            $log.log("[fetch_indicators] 获取策略指标");
            var deferred = $q.defer();
            t ? backend.getIndicator(t.strategy_id).then(function (res) {
                if (res.hasOwnProperty('data') && 0 === res.data.status.code) {
                    var _ind = res.data.data[0];
                    _ind.transact_time *= 1000,
                    gen_indicators(_ind, t),
                    deferred.resolve(t.indicators)
                } else
                deferred.reject()
            }, function () {
                deferred.reject()
            })  : deferred.reject();
            return deferred.promise;
        }
        function gen_indicators(_ind, t) {
            var keys = Object.keys(_ind);
            keys.forEach(function(k) {
                function _get_class(v) {
                    return - 1 !== $.inArray(k, ['nav', 'win_ratio', 'max_drawdown', 'risk_ratio']) ? '': v > 0 ? 'red': 'green'
                }
                if ( - 1 === $.inArray(k, ['strategy_id', 'transact_time'])) {
                    var v = _ind[k],
                    u = '',
                    p = 2; - 1 !== $.inArray(k, ['annual_return', 'max_drawdown', 'risk_ratio', 'profit_ratio', 'win_ratio', 'daily_return']) && (v = 100 * v, u = '%'),
                    -1 !== $.inArray(k, ['trade_count', 'win_count', 'lose_count']) && (p = 0),
                    t.indicators[k] = {
                        name: k,
                        value: v,
                        unit: u,
                       // color_class: _get_class(v),
                        precision: p
                    }
                }
            })
        }
        function fetch_trends(t) {
            var the_promises = [],
            deferred1 = $q.defer();
            backend.getLastNDailyIndicators(t.strategy_id, 60).then(function(resp) {
                var inds = resp.data.data || [];
                inds.length && (t.long_trends.length = 0, inds.reverse().forEach(function(ind) {
                    t.long_trends.push([1000 * ind.transact_time, Number.parseFloat((ind.nav / 10000).toFixed(2))])
                    t.trends.push([1000 * ind.transact_time, Number.parseFloat((ind.nav / 10000).toFixed(2))])
                })),
                deferred1.resolve()
            },
            function() {
                deferred1.reject()
            }),
            the_promises.push(deferred1.promise);
//            var deferred2 = $q.defer();
//            return backend.getIntradayIndicators(t.strategy_id).then(function(resp) {
//                var inds = resp.data.data || [];
//                inds.length && (t.trends.length = 0, inds.forEach(function(ind) {
//                    ind.nav && t.trends.push([1000 * ind.transact_time, ind.nav / 10000])
//                })),
//                deferred2.resolve()
//            },
//            function() {
//                deferred2.reject()
//            }),
//            the_promises.push(deferred2.promise),
            $q.all(the_promises)
        }
        function fetch_trans(t, count){
            var deferred = $q.defer();
            t.trans = [];
            backend.getLastNTransact(t.strategy_id, count).then(function(resp) {
                var _data = [].concat(resp.data.data);
                _data.forEach(function(o){
                    $log.log("[StrategyService.fetch_trans]获取成交信息");
                    t.trans.push(o);
                });
                deferred.resolve(t.orders);
            }, function() {
                $log.log("[StrategyService.fetch_trans]获取成交信息出错");
                deferred.reject();
            });
            return deferred.promise;        }
        function fetch_unfinishedOrders(t) {
            backend.getUnfinishedOrders(t.strategy_id).success(function (resp) {
                $log.log("[StrategyService.fetch_unfinishedOrders]获取未成交信息" + resp.data.data);
                var _data = [].concat(resp.data.data || []);
                t.unfinished_orders = [];
                _data.forEach(function (x) {
                    t.unfinished_orders.push(x);
                })
            });
        }
        function fetch_cash(t){}
        function fetch_accounts(t){}

        var ret = {
            getStrategies: function() {
                $log.log("[StrategyService.getStrategies] 返回策略列表。");
                return list;
            },
            get: function(id) {
                $log.log("[StrategyService.get] 返回指定策略ID的策略, ID 为" + id);
                var s = null;
                list.forEach(function(e){
                    $log.log("遍历策略" + e.strategy_id);
                    if(e.strategy_id == id) {
                        s = e;
                    }
                })
                return s;
            },
            setInitCallBack: function (fun) {
                $log.log("[StrategyService.setInitCallBack]设置初始化回调函数");
                initCb.push(fun)
            },
            fetchPositions: function(strategy) {
                fetch_positions(strategy)
            },
        }
        return ret;

    }])
    .factory('RiskService', ['$q', 'StrategyService', 'gmsdk.td', '$log',function($q, strategyService, backend, $log) {
        $log.log("[RiskService]构造函数");
        return {
            sides:[{id:1,label:"买/多"},{id:2,label:"卖/空"}],
            changeRiskSwitch: function(s) {
                $log.log("TODO: [StrategyService.changeRiskSwitch]打开/关闭策略风控");
                var deferred = $q.defer();
                s.risk_config.enable = !s.risk_config.enable;
                setRiskConfig(s.strategy_id, s.risk_config).then(function () {
                    s.risk_config.enable === !1 && (s.risk_state = null),
                    deferred.resolve();
                }, function () {
                    s.risk_config.enable = !s.risk_config.enable,
                    deferred.reject();
                }),
                deferred.promise;
            },
            setRiskConfig: function(strategy_id, risk_config) {
                var deferred = that.$q.defer();
                backend.setRiskConfig(strategy_id, risk_config).then(function (resp) {
                    var the_strategy = strategyService.get(strategy_id);
                    the_strategy && (the_strategy.risk_config = risk_config);
                    deferred.resolve(resp);
                }, function () {
                    deferred.reject();
                }),
                deferred.promise;
            },
            getRiskConfig: function(strategy_id) {
                return true;
                // return backend.getRiskConfig(strategy_id);
            },
        };
    }])
    .factory('AccountService', ['$rootScope', '$http', 'gmsdk.td', '$log',function($rootScope, $http, backend, $log) {
        var list = [];
        var brokerChannelTypes =[];

        backend.getBrokerChannelTypes().then(function(resp){
            for(var _data = resp.data.data, l = Math.min(_data.length, 10),i = 0; l > i; i++) {
                var x = _data[i];

                brokerChannelTypes.push(x);
                $log.log("初始化经纪通道类型" + x._type + x.title);
           }
        });
        initAccounts();
        function initAccounts() {
            backend.getAccounts().then(function (resp) {
                for(var _data = resp.data.data, l = Math.min(_data.length, 10),i = 0; l > i; i++) {
                    var x = _data[i],
                    t = add_account_base(x);
                    // todo: 从t中获取账户状态等信息
                    fetch_status(t);
                }
            });
        }

        function add_account_base(item) {
            $log.log("[AccountService.add_account_base]增加一个账户, 要修改");
            var t = angular.extend(
                item, {
                    status: {
                        account_type: 3,
                        state:item.id % 3,
                    }
                }
            );

            list.push(t);
            return t;
        }
        function fetch_status(t){}

        var ret = {
            getBrokerChannelTypes: function() {
                $log.log('[AccountService.getAccounts]读取通道类型列表' + brokerChannelTypes.length);
                return brokerChannelTypes;
            },
            getAccounts: function() {
                $log.log("[AccountService.getAccounts]读取账户列表");
                return list;
            },
            fetchAccounts: function() {
                $log.log("[AccountService.getAccounts]读取账户列表");
                list = [];
                initAccounts();
                return list;
            },
            remove: function(id) {
                //$log.log("[AccountService.remove]删除帐号");
                return backend.deleteAccount(id);
            },
            isLoggedIn: function(t) {
                // $log.log("未实现");
                return false;
            },
            account_type: function(id) {
                return '真实';
            },
            update: function(acc) {
                return backend.updateAccount(acc);
            },
            insert: function(acc) {
                return backend.insertAccount(acc);
            },
        };

        return ret;
    }])
    .factory('BacktestService', ['$rootScope', '$q', '$uibModal','gmsdk.td', 'StrategyService', '$log', function ($rootScope, $q, $uibModal, backend, strategyService, $log) {
        var strategies = [],
        inited = false;
        strategyService.setInitCallBack(onStrategyServiceInited.bind(this));
        function onStrategyServiceInited() {
            $log.log('[BacktestService.onStrategyServiceInited] 被调用');
            var _ss = angular.copy(strategyService.getStrategies().map(function (strategy) {
                return {
                    strategy_id: strategy.strategy_id,
                    name: strategy.name,
                    backtests: []
                };
            }));
            _ss.forEach(function(x){
                strategies.push(x);
            });
            $log.log('找到回测的策略' + strategies.length);
        }
        function getBacktestsOfStrategy(strategy) {
            var that = this;
            strategy.hasOwnProperty('backtests') && strategy.backtests.length || (strategy.backtests = []);
            deferred = $q.defer();
            return that.getStrategyBacktest(strategy.strategy_id).then(function(resp) {
                var newBtIds = resp.map(function(bt) {
                    return bt.id
                }),
                len = strategy.backtests.length;
                if (len > 0) for (var i = len - 1; i > -1; i -= 1) - 1 == newBtIds.indexOf(strategy.backtests[i].id) && strategy.backtests.splice(i, 1);
                else strategy.backtests = resp;
                deferred.resolve(resp)
            },
            function(resp) {
                strategy.backtests = [],
                deferred.resolve(resp)
            }),
            deferred.promise
        }
        function getStrategyBacktest(sid) {
            var that = this,
            deferred = that.$q.defer();
            return backend.getStrategyBacktest(sid, parm).then(function(resp) {
                var backtests = resp.data.data;
                deferred.resolve(backtests ? backtests.map(function(backtest) {
                    return that.tidyData(backtest)
                }) : [])
            },
            function() {
                deferred.reject(null)
            }),
            deferred.promise
        }
        return {
            getRawList: function() {
                $log.log('[BacktestService.getRawList]获取所有策略列表');
                return strategies;
            },
            getAllBacktestID: function () {
                var the_promises = [],
                that = this;
                return that.strategies.forEach(function (strategy) {
                    var deferred = that.$q.defer();
                    that.getBacktestsOfStrategy(strategy).then(function () {
                        deferred.resolve()
                    }, function () {
                        deferred.reject()
                    }),
                    the_promises.push(deferred.promise)
                }),
                this.$q.all(the_promises)
            },
            getList: function() {
                $log.log('[BacktestService.getList]获取所有策略列表');
                var that = this,
                deferred = $q.defer();
                return that.inited === !0 ? deferred.resolve(that.strategies) : that.getAllBacktestID().then(function() {
                    that.inited = !0,
                    deferred.resolve(that.strategies)
                },
                function() {
                    that.inited = !1,
                    deferred.reject(that.strategies)
                }),
                deferred.promise
            },
        };
    }])
;
// ============================================= 服务结束 =============================================



// ============================================= directive 开始 =============================================
