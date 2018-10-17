//
//  Strain relief for FTC robot phones - driver station and robot controller
//

use <../common/extrusions.scad>

LARGE = 100; // Something larger than any demension - for O to become U
EPSILON = 0.1; // Small emount to extend so that unions and intersections are clean
base_thick = 1;
cable_thick = 3;


// Phone strain relief WITHOUT the
// cutout to make the phone fit snuggly
module phone_strain_relief0(cable_d) {
    cable_w = 12;
    base_w = 55;
    base_d = 30;
    ro = 2;
    ri = 5;
    cable_pad_h = 1.9; // the base of the lug is slightly elevated (z) from the switch base
    cable_gap_d = 1; // a small gap in x between the start of the center protrusion.
    xoff = (base_w-cable_w)/2;
    support_d = cable_d - cable_gap_d;
    shorten_T_d = cable_d - cable_gap_d - EPSILON - ro;
    translate([xoff, support_d, 0]) rotate([90, 0, 0]) oblong(cable_w, cable_thick + cable_pad_h, ro, support_d);
    translate([0, shorten_T_d, 0]) 
        T_channel(
            cable_w, cable_d - shorten_T_d,
            base_w, base_d, 
            xoff,
            ro, ri, 
            cable_thick);
}

module phone_strain_relief() {
    $fn = 50;
    cable_d = 18;
    curve_r = 8; // Curve around base of phone
    curve_offset = 2;
    difference() {
        phone_strain_relief0(cable_d);
        translate([-EPSILON, cable_d-curve_offset, base_thick]) rotate([90,0, 90]) oblong(LARGE, LARGE, curve_r, LARGE);
    }
}


phone_strain_relief();



