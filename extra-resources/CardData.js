/**
 * Contains the library of cards available for a Cardshifter game in JSON format.
 * Note: Please use the 'effects' wrapper only for properties that change the *behavior* of a card.
 *   Keep regular/arbitrary attributes outside of effects to keep things organized.
 * 
 * @module CardData
 */

function loadCardLibrary() {
    "strict mode";
    let cardLibrary = {
        entities: [        
            //Mech creatures
            {
                name: "Spareparts",
                creature: "Mech",
                health: 1,
                attack: 0,
                sickness: 0,
                attack: 0,
                scrap: 3,
                manaCost: 0,
                flavor: "Cobbled together from whatever was lying around at the time."
            },
            {
                name: "Gyrodroid",
                creature: "Mech",
                health: 1,
                denyCounterAttack: 1,
                attack: 1,
                manaCost: 1,
                scrap: 1,
                flavor: "A flying, spherical droid that shoots weak laser beams at nearby targets."
            },
            {
                name: "The Chopper",
                creature: "Mech",
                health: 1,
                sickness: 0,
                manaCost: 2,
                attack: 2,
                scrap: 1,
                flavor: "Looks like a flying circular blade with a sphere in the middle."
            },
            {
                name: "Shieldmech",
                creature: "Mech",
                health: 3,
                manaCost: 2,
                attack: 1,
                scrap: 1,
                flavor: "A small, flying shield generator droid."
            },
            {
                name: "Robot Guard",
                creature: "Mech",
                health: 2,
                manaCost: 2,
                attack: 2,
                scrap: 1,
                flavor: "Common and inexpensive robot often use for personal protection."
            },
            {
                name: "Humadroid",
                creature: "Mech",
                health: 3,
                manaCost: 3,
                attack: 3,
                scrap: 2,
                flavor: "You might mistake it for a human, but it won’t mistake you for a mech."
            },
            {
                name: "Assassinatrix",
                creature: "Mech",
                health: 1,
                denyCounterAttack: 1,
                manaCost: 3,
                attack: 4,
                scrap: 2,
                flavor: "Humanoid in form, except for two massive cannons in place of arms."
            },
            {
                name: "Fortimech",
                creature: "Mech",
                health: 4,
                manaCost: 3,
                attack: 2,
                scrap: 2,
                flavor: "About the only place that a person is safe during a firefight is inside one of these."
            },
            {
                name: "Scout Mech",
                creature: "Mech",
                health: 1,
                sickness: 0,
                manaCost: 3,
                attack: 5,
                scrap: 2,
                flavor: "The fastest mech on two legs. You don’t want to see the ones with four."
            },
            {
                name: "Supply Mech",
                creature: "Mech",
                health: 5,
                attack: 0,
                sickness: 0,
                manaCost: 3,
                attack: 0,
                scrap: 3,
                flavor: "Worth more than its weight in scrap, and it is pretty heavy.",
            },
            {
                name: "F.M.U.",
                creature: "Mech",
                health: 4,
                attack: 0,
                healTurnEnd: 1,
                manaCost: 4,
                scrap: 2,
                flavor: "The Field Medical Unit is equipped with modern laser surgical tools and a variety of remedy shots."
            },
            {
                name: "Modleg Ambusher",
                creature: "Mech",
                health: 3,
                sickness: 0,
                manaCost: 6,
                attack: 5,
                scrap: 3,
                flavor: "Uses the legs of other bots to enhance its own speed."
            },
            {
                name: "Heavy Mech",
                creature: "Mech",
                health: 6,
                manaCost: 5,
                attack: 3,
                scrap: 3,
                flavor: "The bigger they are, the harder they fall. Eventually."
            },
            {
                name: "Waste Runner",
                creature: "Mech",
                manaCost: 5,
                attack: 4,
                health: 4,
                scrap: 3,
                flavor: "Armored and armed with superior arms.",
            },
            // Bio creatures
            {
                name: "Conscript",
                creature: "Bio",
                health: 2,
                sickness: 0,
                manaCost: 2,
                attack: 2,
                flavor: "He just signed up last week and he is very excited to fight."
            },
            {
                name: "Longshot",
                creature: "Bio",
                health: 1,
                denyCounterAttack: 1,
                manaCost: 3,
                attack: 3,
                flavor: "Eyes and reflexes augmented for maximum deadliness."
            },
            {
                name: "Bodyman",
                creature: "Bio",
                manaCost: 4,
                attack: 2,
                health: 3,
                flavor: "Strength augmented with mechanical musculature."
            },
            {
                name: "Vetter",
                creature: "Bio",
                health: 3,
                manaCost: 5,
                attack: 3,
                flavor: "A retired conscript with a desire to jack and make some quick creds."
            },
            {
                name: "Field Medic",
                creature: "Bio",
                health: 5,
                healTurnEnd: 1,
                manaCost: 5,
                attack: 1,
                flavor: "Unsung hero responsible for keeping countless troops alive."
            },
            {
                name: "Wastelander",
                creature: "Bio",
                health: 4,
                manaCost: 6,
                attack: 4,
                flavor: "Spent his life learning the lessons of the wastelands."
            },
            {
                name: "Commander",
                creature: "Bio",
                health: 3,
                sickness: 0,
                manaCost: 6,
                attack: 5,
                flavor: "A professional soldier for the government."
            },
            {
                name: "Cyberpimp",
                creature: "Bio",
                health: 5,
                manaCost: 6,
                attack: 3,
                flavor: "Supersized and heavily augmented."
            },
            {
                name: "Cyborg",
                creature: "Bio",
                health: 5,
                manaCost: 7,
                attack: 5,
                flavor: "He’s more machine than human now."
            },
            {
                name: "Web Boss",
                creature: "Bio",
                health: 6,
                manaCost: 8,
                attack: 6,
                flavor: "Leader of a gang that primarily operates on the web."
            },
            {
                name: "Inside Man",
                creature: "Bio",
                health: 6,
                attack: 0,
                onCast: {
                    summon: {
                        count: 1,
                        card: "Bodyman",
                        where: "Board",
                        who: "self"
                    }
                }
                // apply:summon:2,"Bodyman",
                manaCost: 8,
                attack: 2,
                flavor: "A government official with wider web control. Usually brings friends."
            },
            // Enchantments
            {
                name: "Bionic Arms",
                enchantment: true,
                addAttack: 2,
                scrapCost: 1,
                flavor: "These arms will give strength to even the most puny individual."
            },
            {
                name: "Body Armor",
                enchantment: true,
                addHealth: 2,
                scrapCost: 1,
                flavor: "Steel-reinforced armor to absord damage from blows and shots."
            },
            {
                name: "Adrenalin Injection",
                enchantment: true,
                addRush: 1,
                addAttack: 1,
                addHealth: 1,
                scrapCost: 1,
                flavor: "An injection to increase speed and body function."
            },
            {
                name: "Steroid Implants",
                enchantment: true,
                addAttack: 2,
                addHealth: 1,
                scrapCost: 2,
                flavor: "Intraveneous implants that feed the body for increased strength."
            },
            {
                name: "Reinforced Cranial Implants",
                enchantment: true,
                addAttack: 1,
                addHealth: 2,
                scrapCost: 2,
                flavor: "Offers head protection as well as a slight increase in brain activity."
            },
            {
                name: "Cybernetic Arm Cannon",
                enchantment: true,
                addRanged: 1,
                addAttack: 3,
                addHealth: 0,
                scrapCost: 2,
                flavor: "Replaces the forearm with a powerful firearm for massive damage."
            },
            {
                name: "Exoskeleton",
                enchantment: true,
                addHealth: 3,
                scrapCost: 2,
                flavor: "This very invasive operation reinforces bone tissue with titanium."
            },
            {
                name: "Artificial Intelligence Implants",
                enchantment: true,
                addAttack: 2,
                addHealth: 3,
                scrapCost: 3,
                flavor: "An advanced processor is connected to the subject's brain, replacing personality with extreme intelligence and reflexes."
            },
            {
                name: "Full-body Cybernetics Upgrade",
                enchantment: true,
                addAttack: 3,
                addHealth: 3,
                scrapCost: 5,
                flavor: "Most of the subject's body is converted to cybernetics, increasing strength and resilience substantially."
            }
        ]
    };
    return cardLibrary;
}


/*
var cardLibrary = loadCardLibrary();

console.log(cardLibrary);
console.log(cardLibrary.entities[0].values);
*/
