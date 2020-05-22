// Frame to mount OLFA rotary cutter on a rail and weigh it down.
// Consists of a back plate, a front plate. Additonally,
// a weight holder (made of wood or polycarb) attaches to the 
// front plate.
//
// History:
//  2020 May 1: JMJ Created original design
//
use <../common/extrusions.scad>
$fn = 50;
EPSILON = 0.01;
LARGE = 100;
MM = 25.4; // Inch to mm
hole_dist = 3.75*MM;
base_w = 0.4*MM; // from hole to edge
ro = 0.5*MM;
rod_dia = 5/32*1.1 *MM; // To let #8 threaded rod fit easily


// Back plate
module back_plate() {
    extra_below = 0.5*MM; // lower part is 1"
    t = 0.28*3; // 3 layers
    r = rod_dia/2;
    hole_y = base_w + extra_below;
    difference() {
        union() {

            wbase = 2*base_w + hole_dist;
            hbase = 2*base_w + extra_below;
            oblong(wbase, hbase, ro, t);
        }
        translate([base_w, hole_y, -EPSILON]) 
            cylinder(r=r, h=2*t);
        translate([base_w + hole_dist, hole_y, -EPSILON]) 
            cylinder(r=rod_dia/2, h=2*t);
    }
}

// Front plate
module front_plate() {
    extra_below = 0.25*MM; // lower part is 1"
    big_r = 1.25*MM; // Arc above
    t = 0.28*3;
    arc_y_drop = 0.125*MM; 
    rod_y = base_w + extra_below;
    hole_y = rod_y - arc_y_drop; // Center of arc - amount below centerline
    big_hole_dia = 1*MM;
    big_hole_h  = 2*MM; // WAS 1.25
    difference() {
        union() {

            wbase = 2*base_w + hole_dist;
            hbase = 2*base_w + extra_below;
             // wide plate
            //oblong(wbase, hbase, ro, t);
            wpad = wbase/6;
            oblong(wpad, hbase, ro, t);
            translate([wbase - wpad, 0, 0])
                oblong(wpad, hbase, ro, t);
            oblong(wbase, hbase*0.7, ro, t);
            
            // Semi circle - upper half
            translate([base_w + hole_dist/2, hole_y, 0]) {
                difference() {
                    cylinder(r=big_r, h = t);
                    translate([-big_r, -2*big_r, -EPSILON])
                        cube([2*big_r, 2*big_r, 2*t]);
                }
            }
        }
        translate([base_w, rod_y, -EPSILON]) 
            cylinder(r=rod_dia/2, h=2*t); // left rod hole
        translate([base_w + hole_dist, rod_y, -EPSILON]) 
            cylinder(r=rod_dia/2, h=2*t); // right rod hole
        bdd = big_hole_dia;
        translate([base_w + hole_dist/2 -bdd/2, hole_y - big_hole_h + bdd/2, -EPSILON])
            oblong(bdd, big_hole_h, bdd, 2*t); // center gap

    }
}


back_plate();
translate([0, 5*base_w, 0])
    front_plate();