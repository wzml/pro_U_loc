package com.example.socket_test.service_test;

public class JudgeBuildId {
    double[] vx = new double[20]; //设一个多边形最多20个顶点
    double[] vy = new double[20];

    double cross1(double dx,double dy,int j,int i){//求叉积
        double bx,by,ax,ay;
        bx = vx[j];by = vy[j];ax = vx[i];ay = vy[i];
        double x1,x2,y1,y2;

        x1 = dx - bx;x2 = ax - bx;
        y1 = dy - by;y2 = ay - by;
        return (x1*y2 - x2*y1);
    }

    double cross2(double dx,double dy,int j,int i){//求向量点积
        double bx,by,ax,ay;
        bx = vx[j];by = vy[j];ax = vx[i];ay = vy[i];
        double x1,x2,y1,y2;

        x1 = dx - ax;x2 = bx - ax;
        y1 = dy - ay;y2 = by - ay;
        return (x1*x2+y1*y2);
    }

    boolean inorout(int n,double px,double py) //奇数个交点在多边形内，偶数个交点在多边形外
    {
        //内侧：true，外侧：false
        int c = 0,i,j;
        for(i = 0,j = n-1;i < n;j = i++)
        {
            double slope = (vy[j] - vy[i]) / (vx[j] - vx[i]);
            boolean above = (py < slope * (px - vx[i]) + vy[i]);
            if(((vx[i]<px && vx[j]>=px) || (vx[i]>=px && vx[j]<px)) && above)
                c++;
        }

        return (c%2 != 0);
    }

    public int Judin(int n,double px, double py){
        //返回1表示在图形中，返回0表示不在
        int i, j;
        for(i = 0;i < n;i++){
            j = i + 1;
            if(i == n-1)
                j = 0;
            if(cross1(px,py,j,i)==0 && cross2(px,py,j,i)>=0 && cross2(px,py,i,j)>=0)
            {
                return 1;
            }
        }
        if(inorout(n,px,py))
            return 1;
        else
            return 0;
    }

    public int JudMain(String str,double locx,double locy) {
        int id1 = -1;
        int len = str.length();

        int n = 0;
        int j = 0;
        char str1[] = str.toCharArray();
        for(int i = 0;i < len;i++) {
            if (str1[i] == '=') {
                n = j;
                if ( n != 0 ) {
                    if(Judin(n,locx,locy) == 1) {
                        return id1;
                    }
                } //待完成 n!=0表示上一个图形顶点记录结束
                i++;//指向id
                id1 = str1[i] - 48;
                i++;//此时指向！
                n = 0;
                j = 0; //顶点编号

                continue;
            }
             //没到下一个id
             if(str1[i] == '+') {
                 String toNum = "";
                 i++;//此时为第一个数字,截取String的一个字符串，并将其转为double
                 while(str1[i] != ',') { //提取x
                     toNum += str1[i];
                     i++;
                 }
                 toNum += '\0';
                 vx[j] = Double.parseDouble(toNum);
                 i++;//y首数字
                 toNum = "";
                 while(str1[i] != '!') { //提取y
                     toNum += str1[i];
                     i++;
                 }
                 toNum += '\0';
                 vy[j] = Double.parseDouble(toNum);
                 //i++; //此时指向下一行的'+'或者‘=’
                 j++; //指向下一个顶点节点
             }

        }

        //最后还有一个图形
        if(Judin(j,locx,locy) == 1) {
            return id1;
        } else
        {
            return -1;
        }

    }

}
