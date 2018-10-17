//
//  Strain relief for FTC robot phones - driver station and robot controller
//

use <../common/extrusions.scad>

$fn = 20;

LARGE = 100; // Something larger than any demension - for O to become U
EPSILON = 0.1; // Small emount to extend so that unions and intersections are clean
base_thick = 1;
cable_thick = 3;

// Phone side - just the bottom,
// tapered up at the lower edge (in x)
module phone_part(base_w) {
    base_thick = 1;
    base_d = 20; // depth - y
    ro = 1; // outside-facing radii
    $fn = 200;
    difference() {
        cube([base_w, base_d, cable_thick]);
        translate([-EPSILON, 0, base_thick]) rotate([90,0, 90]) oblong(50, 50, 10, LARGE);
    }
}

// Support below usb cable - depth - in y-direction -  is {d}
module cable_support(base_w, cable_w, d) {
    //cable_w = 13; // width - x 
    thick = cable_thick;
    cable_h = 11; // height - z
    cable_center_w = 5; // width of center protrusion
    ro = 1;
    cable_r = 1; // radii of curve in center protrusion
    cable_pad_h = 5; // the base of the lug is slightly elevated (z) from the switch base
    cable_gap_d = 10; // a small gap in x between the start of the center protrusion.
                     // This is because the lugs are slightly taller (z) where they
                     // attach to the switch
    
    // The difference is to round the base of the lug - so that tape does not have to
    // wrap around a sharp corner.
    base_r = 10;
    cyl_r = cable_w/2;
    cable_off = (base_w - cable_w)/2; // offset in x of lug position
    
    difference() {

        union() {
            T_channel(cable_w, d, base_w, EPSILON, 
                (base_w-cable_w)/2,
                ro, base_r, thick);
            translate([cable_off, d-cable_gap_d, 0]) rotate([90, 0, 0])
                oblong(cable_w, cable_pad_h+base_thick, cable_r, d - cable_gap_d);
                /*O_channel(cable_w, cable_h, cable_center_w, LARGE, 
                (cable_w - cable_center_w)/2, cable_pad_h+thick,
                cable_r, cable_r,
                d - cable_gap_d);*/
        }
        /*
        translate([-thick, d+EPSILON, -thick]) rotate([90, 0, 0])
            O_channel(cable_w+2*thick, thick+thick, cable_w, LARGE, 
                thick, thick,
                0, cable_r, d+2*EPSILON);*/
    }
}

module phone_strain_relief2() {
    cable_w = 12;
    base_w = 55;
    ro = 5;
    ri = 5;
    base_r = 5;
    cable_d = 30;
    T_channel(cable_w, cable_d, base_w, EPSILON, 
                (base_w-cable_w)/2,
                ro, ri, cable_thick);
}


module phone_strain_relief() {
    base_w = 55;
    cable_w = 10;
    cable_d = 30; // depth - y - of lug
    translate([0, cable_d, 0]) phone_part(base_w);
    cable_support(base_w, cable_w, cable_d);
}


phone_strain_relief2();
// phone_part(55);



