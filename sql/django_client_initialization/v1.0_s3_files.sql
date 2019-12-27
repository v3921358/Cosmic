-- Game Version 1.0
INSERT INTO `dietstory`.`game_versions` (`major_ver`, `minor_ver`, `live_by`, `is_live`) 
VALUES 
('1', '0', now(), '1');

-- Base Game Files
INSERT IGNORE INTO `dietstory`.`game_files` (`file_name`, `s3_path`, `hash_value`, `version_ref_id`) 
VALUES
('Sound_DX8.dll', 'dev/MapleStory_Base/Sound_DX8.dll', 'd9f0f45ae44c3b628603cc08295f370b',1),
('suipre.dll', 'dev/MapleStory_Base/suipre.dll', '78423f5988254bb21229882d229ad059',1),
('v3hunt.dll', 'dev/MapleStory_Base/v3hunt.dll', '26ea11f891ccdea2db0f08190adfb40e',1),
('WzFlashRenderer.dll', 'dev/MapleStory_Base/WzFlashRenderer.dll', '4aee730de72845b7b610d99733710821',1),
('ZLZ.dll', 'dev/MapleStory_Base/ZLZ.dll', 'f158e45530f7e6453b1f0336a2f1c14b',1),
('PCOM.dll', 'dev/MapleStory_Base/PCOM.dll', '4d95ca5ee3187a5f23b47c5688deae40',1),
('ResMan.dll', 'dev/MapleStory_Base/ResMan.dll', 'c32fd9e58087c5ace8314bc7abbf764e',1),
('Shape2D.dll', 'dev/MapleStory_Base/Shape2D.dll', '449a1b736b858ccbcf6548e8f330259b',1),
('aossdk.dll', 'dev/MapleStory_Base/aossdk.dll', '175664cb3579a2dab88e19de5a4c9168', 1), 
('bz32ex.dll', 'dev/MapleStory_Base/bz32ex.dll', '5cdb323b27677dbf07f243e69f7ff0db', 1), 
('Canvas.dll', 'dev/MapleStory_Base/Canvas.dll', '7266a71e6b99ed399e5141ca1d266d05', 1), 
('Dietstory.exe', 'dev/MapleStory_Base/Dietstory.exe', '1f048920ed322ab26f9a33146f6e2d33', 1), 
('Gr2D_DX8.dll', 'dev/MapleStory_Base/Gr2D_DX8.dll', '1a50ca68aed03ce76b65f7c571670416', 1), 
('ijl15.dll', 'dev/MapleStory_Base/ijl15.dll', '4fc074c3c6cf290bb2c11e5c31c97b27', 1), 
('l3codeca.acm', 'dev/MapleStory_Base/l3codeca.acm', '4b4fd61ebb404842eb5823a50a3a58a9', 1), 
('mss32.dll', 'dev/MapleStory_Base/mss32.dll', '71987fa6c12be262d059c0110f9c6b9f', 1), 
('NameSpace.dll', 'dev/MapleStory_Base/NameSpace.dll', '695a6a0ba33c4f9d1109be1821da025b', 1), 
('nmcogame.dll', 'dev/MapleStory_Base/nmcogame.dll', 'fd1a9aad8bb5849e3bb8977795fcef7f', 1), 
('nmconew.dll', 'dev/MapleStory_Base/nmconew.dll', '8d7b8c580525a19e45d94e8c8d8ef79a', 1);


-- WZ Files (Update the md5 hash values if wz files have changed)
INSERT IGNORE INTO `dietstory`.`game_files` (`file_name`, `s3_path`, `hash_value`, `version_ref_id`) 
VALUES 
('Base.wz', 'dev/v1.0/Base.wz', 'f72ee297ea7487c6461f7c3e3daf9fc7', 1),
('Character.wz', 'dev/v1.0/Character.wz', 'c519dd4a621f98bae215f5becc2c6c6b',1),
('Effect.wz', 'dev/v1.0/Effect.wz', 'e86677f146d978ca678d0a4c554fde91',1),
('Etc.wz', 'dev/v1.0/Etc.wz', '35a251b89ea2016ff86303b2c6c96228',1),
('Item.wz', 'dev/v1.0/Item.wz', '4b04b7d12cf5f78a67a1d9b74269d137',1),
('List.wz', 'dev/v1.0/List.wz', '0c9f9a7b1ad490eaa9fa8b66c6e3e7ee',1),
('Map.wz', 'dev/v1.0/Map.wz', '0809f241eae1ed503ee35db87abc0a91',1),
('Mob.wz', 'dev/v1.0/Mob.wz', 'c048370dedbb97da9ebd5bf73b1176e7',1),
('Morph.wz', 'dev/v1.0/Morph.wz', '9c9c287fe19c5ec0e1745224172de526',1),
('Npc.wz', 'dev/v1.0/Npc.wz', '66f443f8c39892e3056975cebf8054ac',1),
('Quest.wz', 'dev/v1.0/Quest.wz', '68ffe349aaa58529c8f3a4e16bf7f448',1),
('Reactor.wz', 'dev/v1.0/Reactor.wz', '62e3956ae43bfccf0ecb050e3a8c2a57',1),
('Skill.wz', 'dev/v1.0/Skill.wz', '7e845fea30631c6f35ad3933fb836c4a',1),
('Sound.wz', 'dev/v1.0/Sound.wz', 'be562e73f00a0d9d6b1b6541a272e4fc',1),
('String.wz', 'dev/v1.0/String.wz', '0d58a1ee1c5025b2fc1db55f803657bd',1),
('TamingMob.wz', 'dev/v1.0/TamingMob.wz', 'e436a62c364a092286b2caf2147c4d8c',1),
('UI.wz', 'dev/v1.0/UI.wz', 'f035353a35c34e282a91b5257c479257',1);