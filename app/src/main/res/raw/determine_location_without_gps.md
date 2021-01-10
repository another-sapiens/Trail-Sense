If you are in an area which doesn't receive GPS signals, you can determine your approximate location via a few different options. Note: This location will not be as accurate as getting a GPS reading, so be careful navigating using these options.

Once an approximate location is determined, you can set it as the GPS override in Trail Sense's GPS settings.

## Triangulation

## Celestial navigation

If you are in the Northern hemisphere, the altitude (or inclination angle) of the North Star (Polaris) is equal to your latitude. Unfortunately, your phone can only give you an approximate estimate of this (using the Inclinometer tool) as typically a device known as a sextant is used.

Determining longitude is a bit more difficult. If your phone has it's time configured properly, you can use the clock tool to get the current time in UTC. You will need to know the exact time of solar noon at your location in UTC time, which can be determined by either when the sun is directly South or North (in the Northern or Southern hemisphere respectively) or when a shadow cast by a stick reaches a minimum length and starts to grow. Once the time of local noon is known in UTC, you can set your GPS override to 0, 0 and get the time of noon at that location using the astronomy tab. Your longitude is the UTC time difference between solar noon at 0, 0 and local noon (represented in hours, with a decimal place) multiplied by 15. In summary, the steps are:

1. Record the time of local solar noon in UTC
2. Set your GPS override to 0, 0
3. Using the astronomy tab, record the time of solar noon
4. Convert the recorded times to hour format: hour + minute / 60 (ex. 13:30 -> 13.5)
5. Calculate the hour offset: noon_at_00 - local_noon
6. Calculate your longitude: hour_offset x 15