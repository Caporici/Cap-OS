#!/bin/sh
# /usr/bin/bootlogo.sh

FB=/dev/fb1
IMG=/usr/share/bootlogo/bootlogo.rgb

# Wait for fb1 to appear for a maximum of 2 seconds (security)
COUNT=0
while [ ! -e "$FB" ] && [ $COUNT -lt 20 ]; do

sleep 0.1

COUNT=$((COUNT + 1))
done

if [ -e "$FB" ]; then

# Clear the cursor and display the image
echo 0 > /sys/class/graphics/fbcon/cursor_blink 2>/dev/null

cat "$IMG" > "$FB"
fi