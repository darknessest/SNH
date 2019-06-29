# SNH

注意一下：这里没有集成开发环境的设置！也没有任何图片。
请标记为exclude 下个文件夹：.idea, venv
最好把之前版本的 .idea 拷到这个项目

### 通知：模型的新版本出来了

&_&:【补充运行须知】
在python下新建data文件夹
在res下新建prepresult文件夹

LH's work:
1.  Add two function in StructExprRecog.java to not use PY or not trust PY with certain conditions
2.  Add some code in StructExprEecog.restruct
3.  Add some Misrecog in wmw.xml
4.  Add one rule in rectifyMisRecogChars1stRnd() to rectify the recognise of tan.

LH's TODO:
1. optimizing the recognising of function set and matrix
2. optimizing the word recognise, like lim, log etc.
3. rectify the obviously misrecog with Gramma analysis.
4. the div character.
5. sometimes a character like \brace maybe recognised as not just one character, like {1,[}, we need to find them before 1.

【模型特点总结】---总体上来说要写的尽量结构清晰。
1、指数和底数大小要合适。
2、积分内部函数位置要适中(积分号写的大一点)。
3、极力避免黏连（过度切分机制还有待研究）。
4、累加累乘的上下界要参考积分的位置来写，并且下标要写成n=1而不是1。
5、矩阵的大括号或小括号要写大一点，包含内部所有元素。

【有待研究】
1、极限怎样才能识别对！！！
2、方程组的大括号一定要识别出来，不然拼不成方程组？
