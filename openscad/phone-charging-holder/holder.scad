// Holder for 4 phones - for the charging station
use <../common/extrusions.scad>
LARGE = 100;
EPSILON = 0.01;
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

iw = 10; // enough to fit phone thickness
thick = 3; // wall thickness
ow = iw + 2*thick;
ri = 3; // internal curvature - should work with phone curvature
ro = 0;
wall_height = 20; // wall height
length = 30; // front-to-back length - along phone should be at least half phone length
dome_r = 4;

module U() {
    O_channel(
    ow, wall_height, 
    iw, LARGE,
    thick, thick,
    ro, ri, length
    );
}

module dome_U() {
    hi = 2*wall_height;
    translate([LARGE-EPSILON,0,0]) rotate([0,-90,0])translate([-thick, 0, 0])
    O_channel(
        length+2*thick, hi/2+2*thick,
        length, hi,
        thick, -hi/2, // offsets
        0, dome_r,
        LARGE
    );
}

module many_Us(n) {
    separation = ow - thick;
    for (i = [0 : n-1]) {
        translate([i*separation, 0, 0]) U();
    }
}

module holder() {
    difference() {
        many_Us(4);
        dome_U();
    }
}

holder();
//dome_U();
//translate ([50, 0, 0]) many_Us(4);