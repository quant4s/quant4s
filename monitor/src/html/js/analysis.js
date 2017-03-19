app.controller('analysisListCtrl', ['$scope', '$http','strategyService', function($scope, $http, strategyService) {
            $scope.strategies = strategyService.list($http, $scope);
        }])
   .controller('analysisDetailCtrl', ['$scope', '$http','$stateParams','strategyService', function($scope, $http, $stateParams,strategyService) {
            var strategy_id=$stateParams.strategy_id?$stateParams.strategy_id:1;

            strategyService.find($http,$scope, strategy_id);
            this.strategy = $scope.strategy;
        }])

//var AnalysisListCtrl=function($scope,StrategyService){
//    $scope.strategies =
//    function highlightCurrent(the_strategy){
//    $scope.strategies.forEach(
//        function(strategy){
//            strategy.is_active=true
//        }
//    );
//    for(var i=0;i<$scope.strategies.length;i++)
//        if($scope.strategies[i].strategy_id===the_strategy.strategy_id){
//            $scope.strategies[i].is_active=!0;
//            break
//        }
//    }
//}
//    $scope.strategies=BacktestService.getRawList(),BacktestService.getList().then(function(strategies){$scope.strategies=strategies});var strategy_id=$scope.$state.params.strategy_id?$scope.$state.params.strategy_id:null;if($scope.selected_strategy=$scope.strategies[0],strategy_id)for(var i=0;i<$scope.strategies.length;i++)if($scope.strategies[i].strategy_id===strategy_id){$scope.selected_strategy=$scope.strategies[i];break}highlightCurrent($scope.selected_strategy)};$traceurRuntime.createClass(AnalysisListCtrl,{},{}),AnalysisListCtrl.$inject=["$scope","BacktestService","StrategyService"];var $__default=AnalysisListCtrl},function(module,exports){"use strict";Object.defineProperties(exports,{"default":{get:function(){return $__default}},__esModule:{value:!0}});

