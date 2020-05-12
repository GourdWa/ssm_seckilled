//存放主要交互逻辑的js代码
//JavaScript 模块化
var seckill = {
    //封装秒杀相关AJAX的url
    URL: {
        now: function () {
            return '/ssm_seckill/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/ssm_seckill/seckill/' + seckillId + '/exposer';
        },
        execution: function (skillId, md5) {
            return '/ssm_seckill/seckill/' + skillId + '/' + md5 + '/execution';
        }

    },
    //验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true
        } else {
            return false;
        }
    },
    handleSeckilllill: function (seckillId, node) {
        //处理秒杀逻辑
        //时间完成后回调事件
        //获取秒杀地址，控制实现逻辑，执行秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>')
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            //在回调函数中，执行交互流程
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    //开启秒杀
                    //获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log('killUrl:' + killUrl);
                    //绑定一次点击事件
                    $("#killBtn").one('click', function () {
                        //执行秒杀请求
                        //1、禁用按钮
                        $(this).addClass('.disabled');
                        //2、发送秒杀请求
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                //显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>')
                                console.log(killResult)
                            }
                            else {
                                console.log("result:" + result['data']['stateInfo'])
                            }
                        });
                    });
                    node.show();
                } else {
                    //未开启秒杀
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    seckill.countdown(seckillId, now, start, end);
                }
            } else {
                console.log(result);
            }
        })
    },
    countdown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $("#seckill-box");
        //时间判断
        if (nowTime > endTime) {
            //秒杀结束
            seckillBox.html('秒杀结束');
        } else if (nowTime < startTime) {
            //秒杀未开始
            //计时事件绑定,续1s
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                //时间格式
                var format = event.strftime('秒杀计时：%D天 %H时 %M分 %S秒')
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                //时间完成后回调事件
                //获取秒杀地址，控制实现逻辑，执行秒杀
                seckill.handleSeckilllill(seckillId, seckillBox);
            });
        } else {
            seckill.handleSeckilllill(seckillId, seckillBox);

        }
    },
    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            //手机验证和登录，计时交互
            var killPhone = $.cookie('killPhone');

            //验证手机号
            if (!seckill.validatePhone(killPhone)) {
                //绑定phone
                //控制输出
                var killPhoneModal = $("#killPhoneModal");
                //显示弹出层
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if (seckill.validatePhone(inputPhone)) {
                        //电话写入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: './seckill.js'});
                        //刷新页面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<lable class="label label-danger">Error Phone</lable>')
                            .show(300);
                    }
                });
            }
            //已经登录
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    //时间判断
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                    // console.log('result:' + nowTime)
                    // console.log(result['success'])
                } else {
                    console.log('result:' + result)
                }
            })
        }
    }
}
