//
//  Strain relief for FTC robot main power switch
//

use <../common/extrusions.scad>

$fn = 20;

thick = 3; // thickness of walls and base
LARGE = 100; // Something larger than any demension - for O to become U
EPSILON = 0.1; // Small emount to extend so that unions and intersections are clean

// Switch side and walls
module switch_part() {
    switch_w = 29; // width - x
    wall_h = 10; // height - z - of walls - will be slightly less than switch walls
                 // so that tape or ziptie is firm against top of the switch
    switch_d = 15; // depth - y
    ro = 1.5; // outside-facing radii
    ri = 0.2; // radii of switch channel inside walls
    side_w = 5; // side part of base without hole
    front_d = 6; // front part of base without hole
    hole_r = 5;
    hole_w = switch_w - 2 * side_w; // width of hole
    base_w = switch_w + 2*thick;
    difference() {
        translate([0, switch_d, 0]) rotate([90, 0, 0])
            O_channel(base_w, wall_h+thick, switch_w, LARGE,
                thick, thick, 
                ro, ri,switch_d);
        translate([thick + side_w, front_d, -EPSILON])
            oblong(hole_w, LARGE, hole_r, thick + 2*EPSILON);
    }
}

// Support below lugs - depth - in y-direction -  is {lug_d}
// There is also a narrow portion that is just for wire strain relief.
// That has depth {lug_ext_d}
module lugs_support(lug_d, lug_ext_d) {
    ro = 0.5; // Convex corners rounding - for T_channel only
    lug_w = 13; // width - x 
    lug_h = 8; // height - z
    lug_ext_h = 10; // Does NOT includ base thickness as it starts from the ground
    lug_center_w = 5; // width of center protrusion
    lug_ext_center_w = 3; // width of narrow part of center protrusion
    lug_r = 1.5; // radii of curve in center protrusion
    lug_pad_h = 0.6; // the base of the lug is slightly elevated (z) from the switch base
    lug_gap_d = 0.5; // a small gap in x between the start of the center protrusion.
                     // This is because the lugs are slightly taller (z) where they
                     // attach to the switch
    
     // The difference is to round the base of the lug - so that tape does not have to
    // wrap around a sharp corner.
    translate([0, lug_ext_d, 0]) {
        difference() {
            union() {
                cube([lug_w, lug_d+EPSILON, thick]);
                translate([lug_w, lug_d-lug_gap_d, lug_h+thick+lug_pad_h])
                    rotate([90, 180, 0])
                        T_channel(lug_center_w, lug_h, lug_w, thick+lug_pad_h, 
                            (lug_w-lug_center_w)/2, 
                            ro, lug_r, lug_d-lug_gap_d);
            }
            translate([-thick, lug_d+EPSILON, -thick]) rotate([90, 0, 0])
                O_channel(lug_w+2*thick, thick+thick, lug_w, LARGE, 
                    thick, thick,
                    0, lug_r, lug_d+2*EPSILON);
        }
    }

    translate([(lug_w - lug_ext_center_w)/2, lug_ext_d + EPSILON, 0]) rotate([90, 0, 0])
        oblong(lug_ext_center_w, lug_ext_h, lug_r, lug_ext_d + EPSILON);
}


module switch_strain_relief() {
    lug_d = 15; // depth - y - of lug
    lug_ext_d = 10; // depth - y - of wire strain relief
    lug_off = 6; // offset in x of lug position
    translate([0, lug_d + lug_ext_d, 0]) switch_part();
    translate([lug_off, 0, 0]) lugs_support(lug_d, lug_ext_d);
}


switch_strain_relief();


