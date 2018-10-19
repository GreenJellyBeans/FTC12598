//
// Enclosure for the larger limit switch, such as this one:
// "Cylewet V-154-1C25 Micro Limit Switch"
//
use <../common/extrusions.scad>
base_thick = 1;
switch_thick = 8;
LARGE = 100;
EPSILON = 0.1;

module switch_base() {
    base_w = 50;
    switch_w = 28 + 0.2;
    switch_d = 16 + 0.2;
    switch_h = 10 + 0.5; // for sticky tape or wire
    switch_off_d0 = 8;
    switch_off_w = 12;
    switch_r = 3;
    top_gap_w = switch_off_w + 18.5;
    top_gap_d = 5;
    top_wall_thick = 3.5;
    hole_dist_x = 16;
    hole_dist_y = 8;
    hole_wall_thick = 3;
    hole_r = 2; // enough for 3/32 (imperial) screws
    top_of_top_hole = 2*hole_wall_thick + hole_r + hole_dist_y + hole_r;
    bot_hole_check = 2*hole_wall_thick + 2*hole_r;
    top_hole_check = top_of_top_hole - (switch_d  - top_gap_d);
    //switch_off_d = max(switch_off_d0, bot_hole_check, top_hole_check);
    switch_off_d = max(switch_off_d0, bot_hole_check);

    bottom_gap_w = switch_off_w + 14;
    bottom_gap_d = switch_off_d + 11;     
    base_d = switch_off_d + switch_d + top_wall_thick;
    tot_thick = base_thick + switch_thick;
    hole1_x = hole_wall_thick + hole_r;
    hole1_y = hole_wall_thick + hole_r;

    ro = 2;
    difference() {
        oblong(base_w, base_d, ro, tot_thick);
        translate([switch_off_w, switch_off_d, base_thick]) 
            oblong(switch_w, switch_d, switch_r, LARGE);
        translate([-EPSILON, switch_off_d + switch_d - top_gap_d, base_thick])
            cube([top_gap_w+EPSILON, LARGE, LARGE]);
        translate([bottom_gap_w, -EPSILON, base_thick])
            cube([LARGE, bottom_gap_d + 2*EPSILON, LARGE]);
        translate([hole1_x, hole1_y, -EPSILON])
            cylinder(r=hole_r, h=LARGE);
        translate([hole1_x, hole1_y + hole_dist_y, -EPSILON])
            cylinder(r=hole_r, h=LARGE);
        translate([hole1_x  + hole_dist_x, hole1_y, -EPSILON])
            cylinder(r=hole_r, h=LARGE);
    }
}

module trial() {
    difference() {
        switch_base();
        translate([25, -EPSILON, -EPSILON]) cube([LARGE, LARGE, LARGE]);
        translate([-EPSILON, -EPSILON, base_thick + 5]) cube([LARGE, LARGE, LARGE]);
    }
}

$fn = 50;
//switch_base();
trial();