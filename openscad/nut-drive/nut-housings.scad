// Housings to hold hexagonal nut for a "nut drive": two nuts with
// reverse threads on a threaded rod.
// All dimensions in mm.
use <../common/extrusions.scad>
EPSILON = 0.01;
LARGE = 100;

function poly_coords(order, r=1)  = 
    let(angles=[ for (i = [0:order-1]) i*(360/order) ])
 	[ for (th=angles) [r*cos(th), r*sin(th)] ];

    
 module regular_polygon(order, r=1){
 	polygon(poly_coords(order, r));
 }


block_w = 30; // Width of block - it's a (rounded) square block
block_ro = 3; // outer radii of rounded block
screw_hole_r = 2.05; // For 3/32 (imperial) screws
holes_major_r = (block_w  - screw_hole_r)/2; // Radius of circle that defines
                                // location of screw holes around center
end_h = 3;
// A rounded square block that fits within {w}x{w} and centered on the origin.
// {r} is the rounding radius.
module block2d(w, r) {
    offset(r = r) square([w-2*r, w-2*r], center=true);
}


// A set of 4 holes positioned on a circle of radius {major_r}.
// Each hole has radius {r}
module holes2d(major_r, r) {
    hole_centers = poly_coords(4, major_r);
    rotate([0, 0, 45]) for (i = [0:3]) {
                translate(hole_centers[i]) 
                    circle(r);
            }
}

// The center piece of the nut block. This holds the hex nut 
// of dia {hex_dia}  and height (thickness of nut) {h}.
// Center is the origin. {hex_dia} is the distance between opposite *corners*
// of the hexagon.
module center_piece(hex_dia, h) {
    linear_extrude(height = h) {
        difference() {
            block2d(block_w, block_ro);
            regular_polygon(6, hex_dia/2);
            holes2d(holes_major_r, screw_hole_r);
        }
    }
}

// Creates an end piece, which matches the center_piece, but with
// an additional rounded triangular piece to hold an anchor screw.
module end_piece() {
    linear_extrude(height = end_h) {
        center_hole_r = 5;
        anchor_offset = 5; // from top of block
        anchor_hole_r = screw_hole_r;
        anchor_wall = 5; // min wall surrounding anchor - this needs to be enough to
                         // take half of the total load on the anchor
        anchor_center = [0, block_w/2 + anchor_offset];
        difference() {
                 //linear_extrude(height = block_h + 2*EPSILON) 
            hull() {
                block2d(block_w, block_ro);
                translate(anchor_center)
                    circle(anchor_wall + anchor_hole_r);
            }
            holes2d(holes_major_r, screw_hole_r);
            circle(center_hole_r);
            translate(anchor_center)
                circle(anchor_hole_r);
            
        }
    }
}


$fn = 50;

// Brass right-handed-thread (RHT) nut dimensions:
//  side 14.12 (measured) == corner 16.304 
//      (using https://rechneronline.de/pi/hexagon.php)
//  nut thickness (measured): 8.52
RHT_hex_dia = 16.304 + 0.4; // 0.4 for clearance
center_h = 8.52 + 0.4; // 0.4 for clearance

// Steel left-hand-thread (LHT) nut dimensions:
// side: 14.17 measured) == corner 16.362  
// nut thickness: 8.4
center_piece(RHT_hex_dia, center_h);
translate([block_w + 10, 0, 0]) end_piece();
// translate([0, block_w + 10, 0]) end_piece();