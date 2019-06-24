# SNH

注意一下：这里没有集成开发环境的设置！也没有任何图片。
请标记为exclude 下个文件夹：.idea, venv
最好把之前版本的 .idea 拷到这个项目

### 通知：请每次都检查TODO表
做好某个TODO任务，请删掉TODO注释，并且在commit message记录

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
