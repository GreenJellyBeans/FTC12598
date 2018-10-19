//
// Enclosure for the larger limit switch, such as this one:
// "Cylewet V-154-1C25 Micro Limit Switch"
//
use <../common/extrusions.scad>
base_thick = 3;
switch_thick = 8;
LARGE = 100;
EPSILON = 0.1;

module switch_base() {
    base_w = 50;
    base_d = 30;
    switch_w = 20;
    switch_d = 10;
    switch_off_d = 5;
    switch_off_w = 5;
    switch_r = 3;
    top_gap_w = switch_off_w + switch_w/2;
    top_gap_h = 4;
    top_gap_d = 4;
    tot_thick = base_thick + switch_thick/2;
    bottom_gap_off_w = switch_off_w + 4;
    bottom_gap_w = 5;
    bottom_gap_h = 4;
    right_gap_off_d = switch_off_d + 3;
    right_gap_d = 5;
    right_gap_h = bottom_gap_h;
    ro = 2;
    difference() {
        oblong(base_w, base_d, ro, tot_thick);
        translate([switch_off_w, switch_off_d, base_thick]) 
            oblong(switch_w, switch_d, switch_r, LARGE);
        translate([-EPSILON, switch_off_d + switch_d - top_gap_d, base_thick])
            cube([top_gap_w+EPSILON, LARGE, LARGE]);
        translate([bottom_gap_off_w, -EPSILON, base_thick])
            cube([bottom_gap_w, switch_off_d + 2*EPSILON, LARGE]);
        translate([switch_off_w+switch_w-EPSILON, right_gap_off_d, base_thick])
            cube([LARGE, right_gap_d, LARGE]);


    }
}



switch_base();