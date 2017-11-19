/*void setup() {
  double y = g(3); 
  System.out.println(y);
  double z = h(3);
  System.out.println(z);
}

public double g(double x){
  return Math.PI*x*x;  
}

public double h(double z){
  return g(z) + g(z) * g(z) + g(z)*g(z)*g(z);
}
*/
void setup() {
  size (800,800);
}
int r = 1;
void draw(){
  ellipse(400,400,r,r);
  while(r<400){
    r++;
  } 
  while(r>0){
    r = r-1;
  }
}