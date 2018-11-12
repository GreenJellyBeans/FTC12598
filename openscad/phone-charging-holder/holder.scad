// Holder for 4 phones - for the charging station
use <../common/extrusions.scad>
LARGE = 100;
EPSILON = 0.01;

iw = 16.5; // enough to fit phone thickness
thick = 3; // wall thickness
ow = iw + 2*thick;
ri = 5; // internal curvature - should work with phone curvature
ro = 0;
wall_height = 35; // wall height
length = 60; // front-to-back length - along phone should be at least half phone length
dome_r = 5;

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