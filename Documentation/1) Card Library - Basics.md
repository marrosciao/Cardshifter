**Cardshifter modding documentation**

---

#Card Library Guide - Basics

Creating and modifying the available cards is the quickest and easiest way to customize your game modification _[mod]_. This guide will explain the basics of what are the different attributes, properties and effects, and how to use them effectively. 

The card library uses a Groovy Domain-Specific Language _[DSL]_ to store card information. Numeric values `(1, 2, 3.14, 42, 999999999)` , as well as Boolean values `(true / false, 1 / 0)` are written without quotes. String of character values `("hello world", "http://www.cardshifter.com/", etc.)` are written either using `'single'` or `"double"` quotation marks (they act the same in the scope of your mod).

---

####Comments

Note that code comments are used throughout to clarify usage. Comments on one line, or at the end of a line, are like `// this is a comment`. Comments spanning multiple lines, or interrupting a line, are like:

    /*
     * This is
     * a multi-line comment
     */
 
Or...
 
    card("My Card") /* a comment */ {
        // stuff
    }

---

###On precise grammar...

It is important to note that the keywords and identifiers must be typed **exactly** as listed to be trustworthy of working. Misspelled words will not work at all. Capilatization must also be respected to ensure functionality.

---

##General attributes

###`card("name")`

- **Required**
- Declares the unique identifier of a specific card. You must not have multiple cards with the same name, otherwise the game behavior could be unpredictable. 
- The properties/attributes/effects pertaining to that card are to all be contained within curly braces `{}` after this is declared.

Usage:

    card("My Card")
    // or
    card('My Card')
    
###`flavor`

- Optional
- Flavor text that says something interesting about the card. 
 
Usage:

    card("My Card") {
            flavor "This is my favorite card of all."
    }
    
---
    
###`creature`

- Any present value declares that the card is a creature. This means that the card can be played onto the battlefield, and will be destroyed into the discard pile upon becoming `health <= 0` or otherwise being destroyed by another mechanic.
- It also displays the creature type of the card.
- For reference, the most common creature types are:

#####_"Cyborg Chronicles"_ mod

- - `"Bio"`: Creature which can have Enchantments played on it. See Enchantments section below for details. 
- - `"Mech"`: Creature which can _not_ have Enchantments played on it. Mechs often have the characteristic that they can be sacrificed for scrap resource. See related `scrap` section. This property requires that `scrap > 0` in order to take effect.

Usage:

    card("My Card") {
        flavor "This is my favorite card of all."
        creature "Bio"
    }

#####_"Mythos"_ mod

- There are no creature-type specific effects at this time. Creature type is used for labeling and targeting.

    card("My Greek Card") {
        flavor "This is my favorite Greek card of all."
        creature "Greek"
    }

---

##Common properties

####`health`

A creature card with `n` health can take `n` damage while it is in battle, and it is destroyed into the discard pile if the health reaches `0` or less. If you set `health: 0`, your card should die immediately after being played, save some effect(s) modify health. Usage: `health: n`

_Note: by default setting, Cardshifter cards will gain their health back after each turn._

####`attack`

A creature card with `n` attack can cause `n` damage  to a target card or player while it is in battle. Usage: `attack: n`

####`scrap`

A creature card with `n` scrap value can be sacrificed from battle after exhausting its casting `sickness`, in exchange for `n` scrap resource to the player. All cards with `scrap > 0` can be sacrificed. `scrap` resource can be used for various things which have a `scrapCost`, including most Enchantments. See the Enchantments section for details. Usage: `scrap: n`

####`sickness`

A creature card with `n` sickness must wait `n` turns after being played before it can perform an action. A creature with `0` sickness is often referred to as having "Rush" or immediate action. Usage: `sickness: n`

####`manaCost`

A card with `n` manaCost requires spending `n` player mana points in order to play it. Usage: `manaCost: n`

####`scrapCost`

A card with `n` scrapCost requires spending `n` player scrap points in order to play it. Usage: `scrapCost: n`

---

##Attack Modifiers

####`noAttack()`

Declares that a creature card cannot attack. 

Usage: `noAttack()`.

####`taunt()`

A card with `taunt()` will require the opponent to attack that card first before other cards or a player can be attacked.

Usage: `taunt()`

####`denyCounterAttack()`

A card with `denyCounterAttack()` will not be subject to the counter-attack mechanic. By default, the counter-attack mechanic makes it so that if a creature attacks another creature, and the target creature has `n` attack, the attacking creature will take `n` damage to health as a consequence. Enabling this will override this mechanic. This can be referred to as a "Ranged" or "Sniper" creature, in a way. 

Usage: `denyCounterAttack()`

---

##Enchantments

Enchantments are cards that can be cast upon creature cards which are in play. They modify the target card's properties.

####`enchantment`

When set to `true`, the card is viewed as an enchantment. Note that a card cannot be both a Creature and an Enchantment. Usage: `enchantment: true`

####`addHealth`

Add `n` health to the target card. Usage: `addHealth: n`

####`addAttack`

Add `n` attack to the target card. Usage: `addAttack: n`

---

**Note**: More advanced effects are described in the `Card Library - Effects.md` file.