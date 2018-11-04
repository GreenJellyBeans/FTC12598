use <../common/extrusions.scad>
LARGE = 100;

module marker_thing() {
    w = 4.5*25.4;
    thick = 3;
    d = 2*25.4;
    h = 2*25.4;
    ri =  3;
    ro = 1;
    
     O_channel(
    w+2*thick, h+thick, 
    w, LARGE, 
    thick, thick, ro, ri, d);
    //cube([w, d, h]);
}

module marker_thing2() {
    w = 4*25.4;
    thick = 3;
    d = 2*25.4;
    h = 4*25.4;
    ri =  3;
    ro = 1;
    
     O_channel(
    w+2*thick, h+2*thick, 
    w, h, 
    thick, thick, ro, ri, d);
    //cube([w, d, h]);
}



marker_thing2();