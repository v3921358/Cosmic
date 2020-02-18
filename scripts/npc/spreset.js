/* @Author Ronan
 * @Author Vcoc
        Name: Steward
        Map(s): Foyer
        Info: Commands
        Script: commands.js
*/
importPackage(Packages.client);
var status;


function start() {
        status = -1;
        action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
            cm.dispose();
    } else {
        if (mode == 0 && type > 0) {
            cm.dispose();
            return;
        }

        if (mode == 1) {
            status++;
        } else{
            status--;
        }

        var player = cm.getPlayer();
        var playerJob = player.getJob().getId();

        // checks for the second digit from right
        // 422 -> 4.22
        var jobBase = playerJob / 100;

        if (status == 0) {
            var sendStr;
            if(player.getJob().isA(MapleJob.BEGINNER) || player.getJob().isA(MapleJob.NOBLESSE) || player.getJob().isA(MapleJob.LEGEND)) {
                sendStr = "As a beginner, you cannot SP reset, sorry."
                cm.sendOk(sendStr);     
            } else {
                sendStr = "Which job's skills would you like to reset?\r\n\r\n#b";
                // checks for the first digit from right
                // 422 -> 2, 420 -> 0
                var jobLevel = playerJob % 10;
                var jobAmount = 0;

                // checks for first job.  If not, increase the amount of jobs based on current job
                if (jobBase % 100 === 0 ) {
                    jobAmount ++;
                } else {
                    jobAmount = jobLevel + 2;
                }
                /*
                    1st Job 00
                    2nd Job x0
                    3rd Job x1
                    4th job x2
                */

                // Generate options based on amount of jobs the character has
                for(var i = 0; i < jobAmount; i++) {
                    if (i === 0) {
                        sendStr += "#L" + i + "#" + "1st Job" + "#l\r\n";
                    }  else if (i === 1) {
                        sendStr += "#L" + i + "#" + "2nd Job" + "#l\r\n";
                    } else if (i === 2) {
                        sendStr += "#L" + i + "#" + "3rd Job" + "#l\r\n";
                    } else if (i === 3) {
                        sendStr += "#L" + i + "#" + "4th Job" + "#l\r\n";
                    }
                }
                
                cm.sendSimple(sendStr);
            }
        } else if(status == 1) {

            var selectedJob;

            // Find the job based on selection
            if(selection == 0) {
                selectedJob = MapleJob.getById(Math.floor(jobBase) * 100);
            } else if(selection == 1) {
                selectedJob = MapleJob.getById(Math.floor(playerJob/10) * 10);
            } else if(selection == 2) {
                selectedJob = MapleJob.getById(Math.floor(playerJob/10) * 10 + 1);
            } else if(selection == 3) {
                selectedJob = MapleJob.getById(Math.floor(playerJob/10) * 10 + 2);
            }

            var additionalSP = 0;
            var jobSkills = SkillFactory.getJobSkillsAsArray(selectedJob);

            // Reset SP for skills attached to selected job
            for(var i = 0; i < jobSkills.length; i++){
                var skillLevel = player.getSkillLevel(jobSkills[i]);
                player.changeSkillLevel(jobSkills[i], 0, player.getMasterLevel(jobSkills[i]), -1)
                additionalSP += skillLevel;    
            }

            // return SP
            player.gainSp(additionalSP);

            cm.sendPrev("Your skills have been reset, and SP has been returned.");
        } else {
            cm.dispose();
        }
    }
}
