'use strict';
var appInfo = {
    host: 'http://localhost:8888'
};
document.title = "招财终端 V" + 1.0;
var app = angular.module('quant4s', ['ui.router', 'ui.bootstrap', 'strategy.filters']);
app.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/');
    $stateProvider
        .state('app', {
            url:'/',
            views: {
                 'header@': {
                     templateUrl:'views/components/header.html',
                     controller: 'headerCtrl',
                 },
                 'sidebar@': {
                     templateUrl:'views/components/sidebar.html',
                     controller: 'sidebarCtrl',
                 },
                'content': {
                    templateUrl:'views/_login.html',
                    controller: 'loginCtrl'
                },
            }
        })
        .state('app.login', {
            url:'/login',
             views: {
               'content': {
                 templateUrl:'views/_login.html',
                 controller: 'loginCtrl'
               },
             }
        })
        .state('app.backtest', {
            url: 'backtest',
            views: {
               'content@': {
                    templateUrl: 'views/backtest/backtest.list.html',
                    controller: 'backtestListCtrl'
                }
            }
        })
        .state('app.backtest.detail', {
            url: '/:id',
            views: {
                'content@': {
                    templateUrl: 'views/_strategy_backtest_detail.html',
                    controller: 'backtestDetailCtrl'
                }
            }
        })
        .state('app.sim', {
            url: 'sim',
            views: {
                'content@': {
                    templateUrl: 'views/strategy/strategy.list.html',
                    controller: 'simCtrl'
                }
            }
        })
        .state('app.strategy', {
            url: 'strategy',
            views: {
                'content@': {
                    templateUrl: 'views/strategy/strategy.list.html',
                    controller: 'realCtrl'
                }
            }
        })
        .state('app.strategy.detail', {
            url: '/:id',
            views: {
                'content@': {
                    templateUrl: 'views/strategy/strategy.detail.html',
                    controller: 'strategyDetailCtrl'
                },
                'sidebar_right@': {
                    templateUrl: 'views/strategy/strategy.sidebar.html',
                    // controller: 'strategyDetailCtrl'
                }
            }
        })
        .state('app.accounts', {
            url: 'accounts',
            views: {
                'content@': {
                    templateUrl: 'views/account/account.list.html',
                    controller: 'accountCtrl'
                }
            }
        })
        .state('app.accounts.info', {
            url: '/:id',
            views: {
                'content@': {
                    templateUrl: 'views/account/account.info.html',
                    controller: 'accountDetailCtrl'
                }
            }
        })
        .state('app.accounts.modify', {
            url: '/:id',
            views: {
                'content@': {
                    templateUrl: 'views/account/account.setting.html',
                    controller: 'accountDetailCtrl'
                }
            }
        })
        .state('app.analysis', {
            url: 'analysis',
            views: {
               'content@': {
                    templateUrl: 'views/analysis/analysis.list.html',
                    controller: 'analysisListCtrl'
                }
            }
        })
        .state('app.analysis.detail', {
             url: '',
             views: {
                'content@': {
                     templateUrl: 'views/analysis/analysis.list.html',
                     controller: 'analysisDetailCtrl'
                 }
             }
         })
        .state('app.help', {
            url: 'help',
            views: {
                'content@': {
                    templateUrl: 'views/_help.html'
                }
            }
        })

    ;

}]);

app.controller('headerCtrl', [function(){

    }])
    .controller('sidebarCtrl', function() {
    })
    .controller('loginCtrl',function($scope, $location) {
        $scope.login = function() {
            $location.path( "backtest" );
        };
    })
    .controller('backtestListCtrl', ['$scope', 'strategyService', function($scope, strategyService) {
        $scope.title = '回测报告';
        $scope.strategies = strategyService.list();
    }])
    .controller('backtestDetailCtrl', ['$scope', '$stateParams', 'strategyService', function($scope, $stateParams, strategyService) {
        $scope.strategy = strategyService.find($stateParams.id);
        alert("hello, back test report" + $stateParams.id);
    }])
    ;


