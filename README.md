# Barcodesearcher
在闲鱼上卖了两条二手内存。寄出去的时候圆通说打印的快递单没有底单给我，让我拍张照片。回头拿照片到网上查快递。查了两遍后觉得人工ocr不爽，如果能直接从图片得到快递单号，自动查询岂不是很方便。<br>
遂做了这个然并卵的东西。<br>
<li>拍照或者直接选取照片。</li><br>
<li>用zbar读取条码（没用zxing，因为zxing的online演示程序没ocr成功，没有仔细研究为什么不成功）。是条码，读出来的是一串数字。不是二维码，二维码包含的内容要多一些。但因为手头没有二维码的快递单，没有写二维码的处理部分。<br>
<li>用kuaidi100的json接口得到快递信息。</li><br>
做到一半的时候发现没什么用处。买家手中没有底单，没有底单拍照功能用不上。至于卖家，卖家在发货之前就先把快递单信息用扫描枪扫入系统中了。只有我这种没扫描枪偶尔发发快递的业余卖家才会有这种需求。</li><br>
功能实现了，bug没有细改，算是学习android的习作吧!<br>
