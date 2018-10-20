//
// Enclosure for the larger limit switch, specifically this one:
// "Cylewet V-154-1C25 Micro Limit Switch"
//
use <../common/extrusions.scad>

LARGE = 100;
EPSILON = 0.1;


module switch_cover() {
    base_thick = 1; // Thickness of "base" plate
    base_ro = 2; // Outer radii of base plate
    switch_thick = 8;
    
    // Switch dimensions
    switch_w = 28 + 0.2;
    switch_d = 16 + 0.2;
    switch_h = 10 + 0.5; // for sticky tape or wire
    
    // Nominal offset in y and x of the lower-left-corner
    // of the switch well.
    switch_off_d0 = 8;
    switch_off_w = 12;
    
    switch_r = 3; // inner radius of switch well    
    top_gap_w = 18.5; // width of gap on the top, from LHS
    top_gap_d = 5; // depth (in y) of the top gap, from the top
    top_wall_thick = 3.5; // thickness (y) of top wall
    right_wall_thick = 3; // thickness (x) of right wall
    
    bottom_gap_w = 14; // width of bottom gap, from RHS
    bottom_gap_d = 11; // depth (in y) of bottom gap, from bottom

    // These are for screw holes
    hole_dist_x = 16; //Distance of screw holes in x == Tetrix spacing
    hole_dist_y = 8;  // Distance of scew holes in y == Lego spacing
    hole_wall_thick = 3; // Min thickness of walls around a hole
    hole_r = 2.05; // Just enough for 3/32 (imperial) screws to slip through easily
    
    // These are minimum distances to check against when computing
    // how much deep (in y) to start the well for the switch - so that there is
    // enough space around each hole in the y-direction
    bot_hole_check = 2*hole_wall_thick + 2*hole_r;
    top_of_top_hole = 2*hole_wall_thick + hole_r + hole_dist_y + hole_r;
    top_hole_check = top_of_top_hole - (switch_d  - top_gap_d);
    switch_off_d = max(switch_off_d0, bot_hole_check, top_hole_check);

    top_gap_end_x = switch_off_w + top_gap_w;
    bottom_gap_end_x = switch_off_w + bottom_gap_w;
    bottom_gap_end_y = switch_off_d + bottom_gap_d;
    base_d = switch_off_d + switch_d + top_wall_thick;
    tot_thick = base_thick + switch_thick;
    hole_x = hole_wall_thick + hole_r;
    hole_y = hole_wall_thick + hole_r;
    base_w = switch_off_w + switch_w + right_wall_thick; // thickness of base
    

    difference() {
        oblong(base_w, base_d, base_ro, tot_thick);
        translate([switch_off_w, switch_off_d, base_thick]) 
            oblong(switch_w, switch_d, switch_r, LARGE);
        translate([-EPSILON, switch_off_d + switch_d - top_gap_d, base_thick])
            cube([top_gap_end_x+EPSILON, LARGE, LARGE]);
        translate([bottom_gap_end_x, -EPSILON, base_thick])
            cube([LARGE, bottom_gap_end_y + 2*EPSILON, LARGE]);
        translate([hole_x, hole_y, -EPSILON])
            cylinder(r=hole_r, h=LARGE);
        translate([hole_x, hole_y + hole_dist_y, -EPSILON])
            cylinder(r=hole_r, h=LARGE);
        translate([hole_x  + hole_dist_x, hole_y, -EPSILON])
            cylinder(r=hole_r, h=LARGE);
    }
}

module trial1() {
    difference() {
        switch_cover();
        translate([-EPSILON, -EPSILON, -EPSILON]) cube([LARGE, 18, LARGE]);
        translate([-EPSILON, -EPSILON, 6]) cube([LARGE, LARGE, LARGE]);
    }
}

module trial2() {
    difference() {
        switch_cover();
        translate([-EPSILON, -EPSILON, -EPSILON]) cube([30, LARGE, LARGE]);
        translate([35, -EPSILON, -EPSILON]) cube([35, LARGE, LARGE]);
        translate([-EPSILON, -EPSILON, 6]) cube([LARGE, LARGE, LARGE]);
    }
}

$fn = 50;
//switch_cover();
translate([60, 0, 0]) trial1();
translate([80, 0, 0]) trial2();