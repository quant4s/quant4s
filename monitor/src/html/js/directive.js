angular.module('custom-flot', []).directive('flotchart', function() {
    return {
        restrict: 'A',
        scope: {
            data: '=data',
            options: '=options'
        },
        link: function(scope, ele) {
            function e(l, t, n) {
                $('<div id="charttooltip" class="flot-tooltip">' + n + '</div>').css({
                    position: 'absolute',
                    top: t - 60,
                    left: l - 65
                }).appendTo('body').show()
            }
            var d = {
                data: scope.data
            },
            plot = $.plot(angular.element(ele), [d], scope.options);
            scope.$watchCollection('data',
            function() {
                d = {
                    data: scope.data
                },
                plot = $.plot(angular.element(ele), [d], scope.options)
            },
            !0);
            var previousPoint = null;
            angular.element(ele).bind('plothover',
            function(event, pos, item) {
                if (item) {
                    if (previousPoint != item.dataIndex) {
                        previousPoint = item.dataIndex,
                        $('#charttooltip').remove();
                        var year, month, day, hour, mins, secs, datetime = new Date(item.datapoint[0]),
                        y = item.datapoint[1].toFixed(2);
                        year = datetime.getFullYear(),
                        month = datetime.getMonth() + 1,
                        day = datetime.getDate(),
                        hour = datetime.getHours(),
                        mins = datetime.getMinutes(),
                        secs = datetime.getSeconds();
                        var time_str = year + '/' + month + '/' + day + '  ' + hour + ':' + mins + ':' + secs;
                        e(item.pageX, item.pageY, time_str + ' <br> ' + y + '万元')
                    }
                } else $('#charttooltip').remove(),
                previousPoint = null
            })
        }
    }
});