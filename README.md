# SNH
注意一下：这里没有集成开发环境的设置！也没有任何图片。
请标记为exclude 下个文件夹：.idea, venv
最好把之前版本的 .idea 拷到这个项目

## 【补充运行须知】
- 在python下新建data文件夹
- 在res下新建prepresult文件夹

## 【模型特点总结】---总体上来说要写的尽量结构清晰。
- 1.指数和底数大小要合适。
- 2.积分内部函数位置要适中(积分号写的大一点)。
- 3.极力避免黏连（过度切分机制还有待研究）。
- 4.累加累乘的上下界要参考积分的位置来写，并且下标要写成n=1而不是1。
- 5.矩阵的大括号或小括号要写大一点，包含内部所有元素。
- 6../.的点要圆一点。

## 【有待研究】
- 1.极限怎样才能识别对！！！
- 2.方程组的大括号一定要识别出来，不然拼不成方程组？

## 【final_change】
- 1.StructExprRecog.java 1406 // 2final_change: x 扩充到 times 和 dottimes
扩增为：
(listBaseULIdentified.get(idx ).mType == UnitProtoType.Type.TYPE_SMALL_X
||listBaseULIdentified.get(idx ).mType == UnitProtoType.Type.TYPE_MULTIPLY
||listBaseULIdentified.get(idx ).mType == UnitProtoType.Type.TYPE_DOT_MULTIPLY)
- 2.UnitProtoypeMgr.java 1619 // 1final_change: TYPE_DOT_MULTIPLY -> TYPE_MULTIPLY
- 3.StructExprRecog.java 4037 // 3final_change: getprincipleser(4)
先去掉首尾匹配，
(此步必要性等待验证：然后内部匹配变为全局匹配(剪切到4062,去掉else))，
两处serDmlChild = mlistChildren.get(i)改为serDmlChild = mlistChildren.get(i).getPrincipleSER(4);
重点!!!:clm中去掉(->1的纠错
